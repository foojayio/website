---
title: "Build a JavaFX Contact Sheet for AI Image Review"
date: "2026-09-30"
description: "Create a responsive JavaFX desktop tool that loads image variants, extracts metadata, and exports a review-ready contact sheet."
authors:
  - "taylor-lee"
image: "javafx-ai-image-review.jpg"
categories:
  - "Java"
  - "JavaFX"
  - "Tutorials"
  - "AI"
related_posts:
  - "search-in-documentation-with-a-javafx-chat-langchain4j-application"
  - "high-performance-rendering-in-javafx"
  - "how-does-java-handle-different-images-and-colorspaces-part-3-introducing-the-bufferedimage"
  - "images-generation-with-quarkus-and-openai"
---

Creative teams rarely evaluate a generated image in isolation. A typical prompt produces several candidates, and the useful work starts when someone compares composition, anatomy, lighting, texture, and crop consistency across the whole batch. Opening files one by one is slow, while dropping them into a general-purpose image editor adds friction to a review that should be quick and repeatable.

This tutorial builds a small JavaFX desktop application for that job. The app scans a folder, decodes images away from the UI thread, presents responsive thumbnail cards, records a reviewer’s rating and note, and exports a contact sheet. The same architecture works for photos, rendered frames, UI screenshots, and other visual assets.

The example deliberately keeps generation outside the Java application. You can create source variants with a browser tool such as [Realistic AI Image Generator](https://realisticaiimagegenerator.online), download the candidates, and then use the desktop app as a local review surface. That separation has two advantages: the Java code never needs an API key, and the review workflow remains useful no matter which system produced the images.

## Project setup

Use JDK 21 and JavaFX 21 or newer. The application needs the controls, graphics, and Swing modules; `javafx.swing` provides the bridge used when writing a composed image through `ImageIO`.

A minimal Maven dependency section looks like this:

```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <javafx.version>21.0.4</javafx.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-swing</artifactId>
        <version>${javafx.version}</version>
    </dependency>
</dependencies>
```

For a modular project, declare the required modules and open the package containing any FXML controllers. This tutorial creates the scene graph in Java, so no FXML module opening is needed.

```java
module io.foojay.imagesheet {
    requires javafx.controls;
    requires javafx.swing;
    requires java.desktop;

    exports io.foojay.imagesheet;
}
```

Keep the first version small. The model only needs a path, an image, a rating, and a note.

```java
public final class ReviewItem {
    private final Path path;
    private final Image image;
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private final StringProperty note = new SimpleStringProperty("");

    public ReviewItem(Path path, Image image) {
        this.path = path;
        this.image = image;
    }

    public Path path() { return path; }
    public Image image() { return image; }
    public IntegerProperty ratingProperty() { return rating; }
    public StringProperty noteProperty() { return note; }
}
```

JavaFX properties are useful here because controls can bind directly to them. A later version could add prompt text, seed, model name, aspect ratio, or any metadata stored beside the image in JSON.

## Load files without freezing the UI

Image decoding can take long enough to make a window feel broken, especially when a folder contains large PNG files. Do not scan and decode on the JavaFX Application Thread. A `Task` provides lifecycle events that are delivered safely back to the UI.

```java
private Task<List<ReviewItem>> loadTask(Path directory) {
    return new Task<>() {
        @Override
        protected List<ReviewItem> call() throws Exception {
            try (Stream<Path> paths = Files.list(directory)) {
                List<Path> files = paths
                    .filter(Files::isRegularFile)
                    .filter(ImageLoader::isSupported)
                    .sorted()
                    .toList();

                List<ReviewItem> result = new ArrayList<>();
                for (int i = 0; i < files.size(); i++) {
                    if (isCancelled()) break;
                    Path file = files.get(i);
                    try (InputStream in = Files.newInputStream(file)) {
                        Image image = new Image(in, 480, 360, true, true);
                        if (!image.isError()) {
                            result.add(new ReviewItem(file, image));
                        }
                    }
                    updateProgress(i + 1, files.size());
                    updateMessage("Loading " + file.getFileName());
                }
                return result;
            }
        }
    };
}
```

The requested width and height act as decode bounds. JavaFX preserves the aspect ratio and performs a smooth resize, so the app does not allocate full-resolution pixel buffers for every thumbnail. Keep original paths in the model; export can reopen full-resolution files if required.

Run the task on a dedicated executor rather than creating unmanaged threads throughout the UI.

```java
private final ExecutorService imageExecutor = Executors.newFixedThreadPool(
    Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
    Thread.ofPlatform().name("image-loader-", 0).factory()
);

private void openFolder(Path folder) {
    Task<List<ReviewItem>> task = loadTask(folder);
    progressBar.progressProperty().bind(task.progressProperty());
    statusLabel.textProperty().bind(task.messageProperty());

    task.setOnSucceeded(event -> {
        items.setAll(task.getValue());
        progressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();
        statusLabel.setText(items.size() + " images loaded");
    });
    task.setOnFailed(event -> showError(task.getException()));
    imageExecutor.execute(task);
}
```

Close the executor in `Application.stop()`. This prevents a background worker from keeping the JVM alive after the last stage closes.

## Build a reusable review card

A card combines an `ImageView`, a filename, a rating control, and a note. Binding the controls to the model means a responsive layout can rebuild or move cards without losing review state.

```java
private Node createCard(ReviewItem item) {
    ImageView preview = new ImageView(item.image());
    preview.setFitWidth(260);
    preview.setFitHeight(195);
    preview.setPreserveRatio(true);
    preview.setSmooth(true);

    Label name = new Label(item.path().getFileName().toString());
    name.setWrapText(true);

    ComboBox<Integer> rating = new ComboBox<>(
        FXCollections.observableArrayList(0, 1, 2, 3, 4, 5)
    );
    rating.valueProperty().bindBidirectional(
        item.ratingProperty().asObject()
    );

    TextArea note = new TextArea();
    note.setPromptText("Composition, anatomy, texture, crop…");
    note.setPrefRowCount(2);
    note.textProperty().bindBidirectional(item.noteProperty());

    VBox card = new VBox(8, preview, name,
        new HBox(8, new Label("Rating"), rating), note);
    card.getStyleClass().add("review-card");
    return card;
}
```

Use a `TilePane` inside a `ScrollPane` for a straightforward responsive grid. A `ListView` with a custom cell is more memory-efficient for thousands of items, but a tile layout is easier to understand and works well for the dozens of variants common in a review session.

```java
TilePane tiles = new TilePane(16, 16);
tiles.setPrefColumns(4);
tiles.setTileAlignment(Pos.TOP_LEFT);

ScrollPane scroll = new ScrollPane(tiles);
scroll.setFitToWidth(true);
scroll.viewportBoundsProperty().addListener((obs, oldBounds, bounds) -> {
    int columns = Math.max(1, (int) (bounds.getWidth() / 300));
    tiles.setPrefColumns(columns);
});
```

Listen to the item list and replace the tile children when loading completes. For larger collections, update in batches to avoid one long scene-graph mutation.

## Preserve aspect ratio and orientation

A contact sheet should not silently distort images. The preview uses `preserveRatio`, but export needs explicit geometry. For each source image, calculate a scale that fits within the cell’s available width and height.

```java
private static Rectangle2D fit(double sourceWidth, double sourceHeight,
                               double boxWidth, double boxHeight) {
    double scale = Math.min(boxWidth / sourceWidth, boxHeight / sourceHeight);
    double width = sourceWidth * scale;
    double height = sourceHeight * scale;
    return new Rectangle2D(
        (boxWidth - width) / 2,
        (boxHeight - height) / 2,
        width,
        height
    );
}
```

If files may come from phones or cameras, remember that JPEG orientation can be stored in EXIF metadata instead of baked into pixels. JavaFX does not expose a complete EXIF API. Either normalize orientation before the files enter the review folder or add a metadata library and rotate during import. Generated images normally arrive already oriented, but a mixed photo-and-render workflow may not.

## Export a contact sheet

JavaFX can render a scene-graph node into a `WritableImage`, but building the export directly with Java2D gives precise control over pagination, text metrics, and output size. The following outline creates a grid on a white background.

```java
public BufferedImage renderSheet(List<ReviewItem> items, int columns) {
    int cellW = 420;
    int cellH = 360;
    int rows = (items.size() + columns - 1) / columns;
    BufferedImage sheet = new BufferedImage(
        columns * cellW, rows * cellH, BufferedImage.TYPE_INT_RGB);

    Graphics2D g = sheet.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, sheet.getWidth(), sheet.getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

        for (int index = 0; index < items.size(); index++) {
            int col = index % columns;
            int row = index / columns;
            drawCell(g, items.get(index), col * cellW, row * cellH,
                     cellW, cellH);
        }
    } finally {
        g.dispose();
    }
    return sheet;
}
```

`drawCell` reopens the original through `ImageIO`, fits it into the image area, and draws filename, rating, and a shortened note beneath it. Wrap long notes by words and clip text to the cell. Export on a background executor because full-resolution decoding and JPEG compression are expensive.

```java
Path output = chooser.showSaveDialog(stage).toPath();
Task<Path> export = new Task<>() {
    @Override
    protected Path call() throws Exception {
        BufferedImage sheet = renderSheet(List.copyOf(items), 4);
        ImageIO.write(sheet, "jpg", output.toFile());
        return output;
    }
};
export.setOnSucceeded(e -> statusLabel.setText(
    "Saved " + export.getValue().getFileName()));
imageExecutor.execute(export);
```

For very large batches, generate multiple pages instead of one enormous bitmap. A practical limit is based on pixel count, not just item count: a 20,000-by-20,000 RGB sheet already requires about 1.2 GB before encoder overhead.

## Make review decisions reproducible

A picture alone does not explain why a reviewer chose it. Export a small CSV or JSON file beside the sheet with the relative path, rating, note, and review time. Stable filenames make it possible to compare decisions after a new generation round.

```java
record ReviewRecord(String file, int rating, String note) {}

List<ReviewRecord> records = items.stream()
    .map(item -> new ReviewRecord(
        item.path().getFileName().toString(),
        item.ratingProperty().get(),
        item.noteProperty().get()))
    .toList();
```

Do not infer technical truth from a plausible-looking image. Generated visuals can contain inconsistent text, reflections, anatomy, materials, and perspective. The review UI should help people flag those defects, not hide them. If an image depicts a real person, event, product, or newsworthy scene, preserve provenance and follow the disclosure policy of the publication or organization using it.

## Useful extensions

Once the core workflow is reliable, several additions fit naturally:

- Watch the folder with `WatchService` and append new variants as they arrive.
- Hash files with SHA-256 to detect duplicates even when filenames differ.
- Read JSON sidecars containing prompt, seed, model, and dimensions.
- Add keyboard shortcuts for ratings so reviewers can work without a mouse.
- Persist sessions locally with SQLite and reopen unfinished reviews.
- Export an HTML contact sheet with links to originals instead of one large JPEG.
- Add an optional similarity grouping stage, but keep automated rankings visibly separate from human ratings.

The key design principle is separation of concerns. Loading, review state, layout, and export should remain independent. That makes the application easier to test and lets the same JavaFX interface review assets from any image-generation or rendering pipeline.

## Closing thoughts

JavaFX is a good fit for a local visual-review utility: it provides fast image presentation, property binding, accessible controls, and straightforward desktop file access without requiring a web service. By decoding thumbnails off the UI thread, retaining original paths, and exporting both visual and structured review results, a small application can turn a folder of loosely organized variants into a consistent editorial workflow.

Start with a few files and instrument memory use before scaling. The best contact-sheet tool is not the one with the most automated scoring; it is the one that helps a reviewer compare the right evidence, record a clear decision, and reproduce that decision later.