---
title: "Integrating Google Analytics With Vaadin Flow: A Step-By-Step Guide"
date: "2025-01-08T11:55:48+00:00"
lastmod: "2025-01-09T17:13:27+00:00"
description: "Want to track how users interact with your Vaadin Flow application? Learn how to integrate Google Analytics with a clean, reusable component."
authors:
  - "simon-martinelli"
image: "VaadinLogo_RGB_1000x310.png"
categories:
  - "Java"
  - "Vaadin"
related_posts:
  - "5-great-reasons-to-use-jooq"
  - "a-faster-way-to-build-react-spring-boot-apps-using-hilla-1-3"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
frozen: false
---

**Want to track how users interact with your Vaadin Flow application? Learn how to integrate Google Analytics with a clean, reusable component. This guide shows you step-by-step instructions on how to set up tracking, monitor page views, and capture custom events in your Vaadin application.**

Analytics are crucial for understanding how users interact with your web application. In this guide, I'll show you how to integrate Google Analytics with your Vaadin Flow application in a clean, maintainable way.

## Prerequisites

Before we start, you'll need:

* A Vaadin Flow application
* A Google Analytics account with a measurement ID (usually starts with "G-")
* Basic understanding of Vaadin components

## Creating the Analytics Component

First, let's create a reusable component that handles the Google Analytics integration. This component will:

* Initialize Google Analytics
* Track page views
* Allow sending custom events

Here's the complete implementation:

```java
@Tag("google-analytics")
public class GoogleAnalytics extends Component implements HasSize {

    public GoogleAnalytics(String measurementId) {
        getElement().executeJs(
            """
            (function(browserWindow, htmlDocument, scriptTagName, analyticsLayerName, analyticsId) {
                // Initialize analytics data layer array if it doesn't exist
                browserWindow[analyticsLayerName] = browserWindow[analyticsLayerName] || [];

                // Add initial GTM event with timestamp
                browserWindow[analyticsLayerName].push({
                    'gtm.start': new Date().getTime(),
                    event: 'gtm.js'
                });

                // Get reference to first script tag in document
                var firstScriptTag = htmlDocument.getElementsByTagName(scriptTagName)[0];

                // Create new script element for Analytics
                var analyticsScript = htmlDocument.createElement(scriptTagName);

                // Set custom layer name if not using default 'dataLayer'
                var customLayerParam = analyticsLayerName != 'dataLayer' ? '&l=' + analyticsLayerName : '';

                // Configure script loading
                analyticsScript.async = true;
                analyticsScript.src = 'https://www.googletagmanager.com/gtag/js?id=' + analyticsId + customLayerParam;

                // Insert Analytics script into document
                firstScriptTag.parentNode.insertBefore(analyticsScript, firstScriptTag);

            })(window, document, 'script', 'dataLayer', '$0');

            // Ensure dataLayer exists
            window.dataLayer = window.dataLayer || [];

            // Function to send data to Analytics
            function sendToAnalytics() {
                window.dataLayer.push(arguments);
            }

            // Initialize Analytics with timestamp
            sendToAnalytics('js', new Date());

            // Configure Analytics with measurement ID
            sendToAnalytics('config', '$0');
            """, measurementId
        );
    }
    public void sendPageView(String pageName) {
        getElement().executeJs(
            """
            sendToAnalytics('event', 'page_view', {
                'page_title': $0,
                'page_location': window.location.href,
                'page_path': window.location.pathname
            });
            """, pageName
        );
    }
    public void sendEvent(String eventName, JsonObject eventParams) {
        getElement().executeJs(
            "sendToAnalytics('event', $0, $1);",
            eventName, eventParams
        );
    }
}
```

## Using the Component

### Step 1: Add To Main Layout

Add the Google Analytics component to your main layout to initialize tracking:

```java
public class MainLayout extends AppLayout {
    public MainLayout() {
        GoogleAnalytics analytics = new GoogleAnalytics("G-XXXXXXXXXX"); // Your measurement ID
        addToDrawer(analytics);

        // Track page views when route changes
        UI.getCurrent().addBeforeEnterListener(event -> {
            analytics.sendPageView(event.getLocation().getPath());
        });
    }
}
```

### Step 2: Track Custom Events

You can track custom events anywhere in your application:

```java
@Route("user-profile")
public class UserProfileView extends VerticalLayout {
    private final GoogleAnalytics analytics;

    public UserProfileView(GoogleAnalytics analytics) {
        this.analytics = analytics;

        Button saveButton = new Button("Save Profile", event -> {
            // Save profile logic here

            // Track the save event
            JsonObject params = Json.createObject();
            params.put("category", "profile");
            params.put("action", "save");
            analytics.sendEvent("profile_update", params);
        });
    }
}
```

## How It Works

The component uses Vaadin's JavaScript execution capabilities to:

1. Load the Google Analytics script asynchronously
2. Initialize the data layer
3. Set up tracking functions
4. Send events to Google Analytics

The JavaScript code is structured for readability and maintainability, with clear variable names and comments explaining each step.

## Best Practices

1. Initialize analytics only once in your main layout
2. Use descriptive event names and parameters
3. Track meaningful user interactions
4. Consider user privacy and GDPR compliance
5. Add error handling for analytics calls

## Common Use Cases

Here are some events you might want to track:

* Form submissions
* Button clicks
* Feature usage
* Error occurrences
* User preferences

## Conclusion

This integration provides a clean, type-safe way to use Google Analytics in your Vaadin Flow application. The component approach makes it easy to maintain and extend the tracking functionality as needed.

Remember to:

* Replace the measurement ID with your actual Google Analytics ID
* Test the tracking in your development environment
* Consider adding a cookie consent mechanism if needed
* Review Google Analytics documentation for advanced usage

Happy tracking!
