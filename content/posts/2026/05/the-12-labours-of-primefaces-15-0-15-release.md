---
title: "💪😤 THE 12 LABOURS OF PRIMEFACES 15.0.15 #release"
slug: "the-12-labours-of-primefaces-15-0-15-release"
date: "2026-05-10T08:15:26+00:00"
lastmod: "2026-05-10T15:14:28+00:00"
description: "💪😤 THE 12 LABOURS OF PRIMEFACES 15.0.15 #release"
authors:
  - "vincent-vauban"
image: "https://foojay.io/wp-content/uploads/2026/05/toge-herculer-1024x576.png"
categories:
  - "Jakarta EE"
  - "Java"
tags:
related_posts:
enlighterjs: true
frozen: false
---

<br />

<img fetchpriority="high" decoding="async" class="size-medium wp-image-123733" src="/images/posts/2026/05/the-12-labours-of-primefaces-15-0-15-release/toge-herculer-700x394.png" alt="💪😤 THE 12 LABOURS OF PRIMEFACES 15.0.15 #release" width="700" height="394">

<br />

💪😤 THE 12 LABOURS OF PRIMEFACES 15.0.15 #release

<br />

### PrimeFaces 15.0.15 has just been released. 🚀 {#h3-0-primefaces-15-0-15-has-just-been-released}

And no, this is not "React for Java".

PrimeFaces is a component library for JSF / Jakarta Faces.

Its goal is simple:
> build server-side Java web applications with rich UI components without writing everything manually in HTML, CSS and JavaScript.

This release is mostly about polish:

▪️ security hardening  

▪️ accessibility fixes  

▪️ better component behavior  

▪️ fewer UI edge cases  

▪️ smoother developer experience

Let's look at what changed, then at the most common PrimeFaces concepts with small code examples. 👇  
[https://github.com/primefaces/primefaces/releases/tag/v15.0.15](http://https://github.com/primefaces/primefaces/releases/tag/v15.0.15 "https://github.com/primefaces/primefaces/releases/tag/v15.0.15")

*** ** * ** ***

### 🔵 TL;DR {#h3-1-tl-dr}

#### A quick overview of why PrimeFaces 15.0.15 matters and what kind of improvements it brings.

▪️ PrimeFaces 15.0.15 is a maintenance release, not a "big feature" release  

▪️ The most visible change is the new escape behavior on p:schedule tooltips  

▪️ Accessibility keeps improving around ARIA attributes  

▪️ Several components received small but useful fixes: SelectOneMenu, Panel, ConfirmDialog, TextEditor, BlockUI, AutoComplete, Slider  

▪️ PrimeFaces remains a good fit when your application is strongly Java/server-side oriented

*** ** * ** ***

🔵 WHAT PRIMEFACES 15.0.15 BRINGS {#h2-2-what-primefaces-15-0-15-brings}
------------------------------------------------------------------------

### 🔵 1. SCHEDULE TOOLTIP ESCAPING {#h3-3-1-schedule-tooltip-escaping}

#### PrimeFaces now gives better control over how tooltip content is escaped in p:schedule.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:schedule value="#{calendarBean.eventModel}"
            tooltip="true"
            escape="true" /&gt;</pre>

The escape property controls whether HTML content in schedule tooltip descriptions is escaped.

##### Explanation:

▪️ escape="true" is the safe default  

▪️ HTML is rendered as text  

▪️ this helps avoid unwanted HTML injection in tooltips  

▪️ if you explicitly need trusted HTML, you can opt out carefully
> For newbies: do not set escape="false" just because "it looks nicer". Only do it when you fully control and sanitize the content. 🔐

*** ** * ** ***

### 🔵 2. SCHEDULE TOOLTIP WITH TRUSTED HTML {#h3-4-2-schedule-tooltip-with-trusted-html}

#### HTML tooltips can still be rendered when the content is trusted and properly controlled.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:schedule value="#{calendarBean.eventModel}"
            tooltip="true"
            escape="false" /&gt;</pre>

#### Explanation:

This allows richer tooltip rendering when event descriptions contain HTML.

#### Useful for:

▪️ internal dashboards  

▪️ trusted admin content  

▪️ formatted calendar events

But it comes with responsibility.

If the content comes from users, databases, imports, or external systems, escaping should stay enabled.
> Security is not an aesthetic option. 😉

*** ** * ** ***

### 🔵 3. SELECTONEMENU ACCESSIBILITY {#h3-5-3-selectonemenu-accessibility}

#### SelectOneMenu gets improved ARIA behavior for better accessibility support.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:outputLabel for="country" value="Country" /&gt;

&lt;p:selectOneMenu id="country"
                 value="#{userBean.country}"
                 required="true"
                 ariaLabel="Country"&gt;
    &lt;f:selectItem itemLabel="Select one" itemValue="" /&gt;
    &lt;f:selectItems value="#{userBean.countries}" /&gt;
&lt;/p:selectOneMenu&gt;</pre>

#### Explanation:

PrimeFaces 15.0.15 restores some ARIA attributes on SelectOneMenu.

#### Why it matters:

▪️ screen readers understand the component better  

▪️ invalid/required states are clearer  

▪️ forms are more accessible  

▪️ enterprise applications become more usable for everyone ♿
> Accessibility is not decoration. It is part of the component contract.

*** ** * ** ***

### 🔵 4. PANEL TOGGLE HEADER BEHAVIOR {#h3-6-4-panel-toggle-header-behavior}

#### Panel headers now expose more accurate accessibility behavior when toggleable headers are used.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:panel header="Advanced filters"
         toggleable="true"
         toggleableHeader="true"&gt;

    &lt;p:inputText value="#{searchBean.keyword}" /&gt;
&lt;/p:panel&gt;</pre>

#### Explanation:

The Panel fix makes ARIA behavior more precise.

The header should behave like a button only when the header is actually toggleable.

#### Why it matters:

▪️ better semantics  

▪️ less confusing screen reader output  

▪️ cleaner keyboard navigation  

▪️ more predictable UI behavior
> Small fix, real UX impact.

*** ** * ** ***

### 🔵 5. CONFIRM BEFORE SHOW CALLBACK {#h3-7-5-confirm-before-show-callback}

#### Confirmation dialogs can better respect logic executed before the dialog is displayed.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;script&gt;
function canShowDeleteConfirm() {
    return confirm('Do you really want to open the confirmation dialog?');
}
&lt;/script&gt;

&lt;p:commandButton value="Delete"
                 action="#{userBean.delete}"&gt;
    &lt;p:confirm header="Confirmation"
               message="Delete this user?"
               icon="pi pi-exclamation-triangle"
               beforeShow="return canShowDeleteConfirm();" /&gt;
&lt;/p:commandButton&gt;

&lt;p:confirmDialog global="true" /&gt;</pre>

#### Explanation:

The beforeShow callback is now properly respected.

That means you can decide before the confirmation popup opens.

#### Useful for:

▪️ business pre-checks  

▪️ client-side conditions  

▪️ avoiding unnecessary dialogs  

▪️ custom UX flows
> It is a small fix, but it makes confirmation flows more reliable.

*** ** * ** ***

### 🔵 6. INPUTNUMBER AND AUTONUMERIC UPDATE {#h3-8-6-inputnumber-and-autonumeric-update}

#### Numeric inputs benefit from improvements around formatting, precision and user interaction.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:inputNumber value="#{invoiceBean.amount}"
               symbol="€"
               decimalPlaces="2"
               thousandSeparator=" "
               decimalSeparator="," /&gt;</pre>

#### Explanation:

PrimeFaces 15.0.15 updates/fixes behavior around AutoNumeric, the JavaScript library behind p:inputNumber.

#### Why developers care:

▪️ numeric input is tricky  

▪️ currencies are tricky  

▪️ decimal separators are tricky  

▪️ undo/redo, backspace, formatting and parsing must stay consistent
> This is the kind of fix users may never notice...because the component simply behaves correctly. ✅

*** ** * ** ***

### 🔵 7. TEXTEDITOR PASTE CLEANUP {#h3-9-7-texteditor-paste-cleanup}

#### Text pasted into the editor is handled more cleanly, especially around invisible spacing issues.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:textEditor value="#{articleBean.content}"
              height="300" /&gt;</pre>

#### Explanation:

The TextEditor fix handles non-breaking spaces during paste operations.

#### Why it matters:

▪️ copy-paste from Word, websites or rich editors can introduce invisible characters  

▪️ invisible characters create formatting surprises  

▪️ text storage and rendering become cleaner  

▪️ content editing feels less buggy
> Rich text editors are never "just text". This fix helps reduce one of those annoying content-editing edge cases.

*** ** * ** ***

### 🔵 8. PANELMENU STATEFULNESS {#h3-10-8-panelmenu-statefulness}

#### Nested menu items better remember their expanded or collapsed state after navigation.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:panelMenu stateful="true"&gt;
    &lt;p:submenu label="Administration"&gt;
        &lt;p:submenu label="Users"&gt;
            &lt;p:menuitem value="Search users"
                        outcome="/admin/users" /&gt;
        &lt;/p:submenu&gt;
    &lt;/p:submenu&gt;
&lt;/p:panelMenu&gt;</pre>

#### Explanation:

The release improves statefulness for nested menu items. (submenus better remember their expanded/collapsed between navigations)

#### Why it matters:

▪️ expanded menu state is preserved better  

▪️ nested navigation becomes less frustrating  

▪️ admin dashboards feel more stable  

▪️ users do not lose their navigation context
> In back-office applications, navigation stability matters a lot.

*** ** * ** ***

### 🔵 9. AJAX ERROR HANDLING {#h3-11-9-ajax-error-handling}

#### Ajax behavior is improved around redirects and error situations during partial page updates.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:commandButton value="Save"
                 action="#{profileBean.save}"
                 process="@form"
                 update="messages profilePanel" /&gt;

&lt;p:messages id="messages" /&gt;</pre>

#### Explanation:

PrimeFaces 15.0.15 improves Ajax handling around redirect/error edge cases.

PrimeFaces apps rely heavily on partial page updates.

#### That means small Ajax bugs can create strange UI effects:

▪️ blocked components  

▪️ incomplete refreshes  

▪️ broken redirect behavior  

▪️ confusing user feedback
> This fix is mostly invisible, but important for robustness.

*** ** * ** ***

### 🔵 10. BLOCKUI CLEANUP {#h3-12-10-blockui-cleanup}

#### Blocked UI areas are cleaned up more reliably after Ajax updates and widget lifecycle changes.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:blockUI block="formPanel"
           trigger="saveButton" /&gt;

&lt;p:panel id="formPanel"&gt;
    &lt;p:commandButton id="saveButton"
                     value="Save"
                     action="#{profileBean.save}"
                     update="formPanel" /&gt;
&lt;/p:panel&gt;</pre>

#### Explanation:

BlockUI is used to prevent user interaction while an Ajax request is running.

The fix ensures the target element is properly unlocked during widget cleanup.

#### Why it matters:

▪️ fewer "stuck UI" situations  

▪️ better behavior after partial updates  

▪️ cleaner Ajax lifecycle  

▪️ less frustration for users
> A blocked screen after a save button is one of the fastest ways to lose user confidence.

*** ** * ** ***

### 🔵 11. AUTOCOMPLETE MORETEXT FIX {#h3-13-11-autocomplete-moretext-fix}

#### Autocomplete now handles the "more results" message more consistently.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:autoComplete value="#{cityBean.selectedCity}"
                completeMethod="#{cityBean.completeCity}"
                var="city"
                itemLabel="#{city.name}"
                itemValue="#{city}"
                moreText="More results available..." /&gt;</pre>

#### Explanation:

The moreText area is used when more suggestions exist than currently displayed.

This fix improves how that text is rendered and exposed.

#### Useful when:

▪️ suggestions are limited  

▪️ search results are large  

▪️ users need feedback  

▪️ accessibility matters
> Autocomplete is not only about search. It is about guiding the user.

*** ** * ** ***

### 🔵 12. SLIDER PRECISION {#h3-14-12-slider-precision}

#### Slider values are displayed with precision that better matches the configured step.

<pre class="EnlighterJSRAW" data-enlighter-language="html">&lt;p:inputText id="discount" value="#{pricingBean.discount}" /&gt;

&lt;p:slider for="discount"
          minValue="0"
          maxValue="1"
          step="0.05"
          display="discountOutput"
          displayTemplate="{value}" /&gt;

&lt;h:outputText id="discountOutput" /&gt;</pre>

#### Explanation:

The Slider display now uses the same precision as the configured step.

#### Example:

▪️ step = 0.05  

▪️ display should remain consistent with that precision  

▪️ no strange rounding surprises  

▪️ better display for percentages, ratings, thresholds and numeric filters
> Small detail. Big difference when users manipulate numbers.

*** ** * ** ***

### 🔵 TAKEAWAYS {#h3-15-takeaways}

#### Small maintenance fixes can have a meaningful impact on security, accessibility and daily user experience.

▪️ PrimeFaces is still relevant when you want server-side Java UI development  

▪️ It is especially useful for enterprise CRUD, admin panels, internal tools and data-heavy applications  

▪️ PrimeFaces 15.0.15 is mostly about quality, not hype  

▪️ Security and accessibility fixes matter as much as shiny components  

▪️ New developers should understand the JSF lifecycle before judging PrimeFaces  

▪️ The real power is not "drag and drop UI" --- it is the combination of Java beans, components, Ajax updates and rich widgets
> PrimeFaces is not the trendy kid in the frontend room. But in many Java enterprise applications, it is still doing serious work. ☕🎨

##### Java #JakartaEE #JSF #PrimeFaces #JakartaFaces #EnterpriseJava #WebDevelopment #JavaDevelopers #SoftwareEngineering #Accessibility #WebSecurity #BackendDevelopment #FullStackJava

*** ** * ** ***

### Go further with Java certification: {#h3-16-go-further-with-java-certification}

#### Java👇

<https://bit.ly/javaOCP>

#### Spring👇

<https://bit.ly/2v7222>

#### SpringBook👇

<https://bit.ly/springtify>

#### JavaBook👇

<https://bit.ly/jroadmap>
