{{- /* An article as Markdown, for index.md beside its HTML. The body is the
       source as written: shortcodes stay unexpanded, and an AsciiDoc article
       stays AsciiDoc. Bundle images keep their relative paths, which resolve
       because this file is served from the page's own folder. */ -}}
# {{ .Title }}

{{ with .Description }}> {{ . }}

{{ end -}}
- URL: {{ .Permalink }}
{{ if eq .Section "posts" }}- Published: {{ .Date.Format "2006-01-02" }}
{{ with .Params.authors }}- Authors: {{ range $i, $a := . }}{{ if $i }}, {{ end }}{{ with partial "author-page.html" $a }}{{ .Title }}{{ else }}{{ $a }}{{ end }}{{ end }}
{{ end }}{{ with .Params.categories }}- Categories: {{ delimit . ", " }}
{{ end }}{{ with .Params.canonical }}- Originally published at: {{ . }}
{{ end }}{{ if and .File (eq .File.Ext "adoc") }}- Source format: AsciiDoc
{{ end }}{{ end }}
{{ .RawContent }}
