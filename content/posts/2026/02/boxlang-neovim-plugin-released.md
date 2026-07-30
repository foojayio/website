---
title: "BoxLang NeoVim Plugin Released"
slug: "boxlang-neovim-plugin-released"
date: "2026-02-18T14:51:35+00:00"
lastmod: "2026-02-19T08:42:51+00:00"
description: "We're excited to announce the release of the BoxLang NeoVim Plugin - a comprehensive syntax highlighting solution designed specifically for BoxLang - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "/images/posts/2026/02/boxlang-neovim-plugin-released/boxlang-neovim.jpg"
categories:
  - "BoxLang"
  - "Developer Tools"
  - "Tools"
tags:
related_posts:
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-1-14-0-navigate-anything-jsonpath-comes-to-boxlangs-datanavigator"
  - "boxlang-1-14-0-query-transformers-take-full-control-of-your-query-results"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
enlighterjs: true
frozen: false
---

![](/images/posts/2026/02/boxlang-neovim-plugin-released/boxlang-neovim-700x467.jpg)

We're excited to announce the release of the BoxLang NeoVim Plugin - a comprehensive syntax highlighting solution designed specifically for BoxLang developers working in Vim and NeoVim environments. This isn't a port or adaptation of existing CFML syntax files; it's a ground-up implementation built for BoxLang's modern feature set. Coming soon as well will be our runners, syntax validators, and integration with our LSP for live previews, insights, and much more.

Why a Dedicated BoxLang Plugin?Why a Dedicated BoxLang Plugin? {#h2-0-why-a-dedicated-boxlang-plugin-why-a-dedicated-boxlang-plugin}
------------------------------------------------------------------------------------------------------------------------------------

![](/images/posts/2026/02/boxlang-neovim-plugin-released/vim-boxlang-class-400x510.png)

BoxLang is a modern dynamic JVM language that combines features from Java, CFML, Python, Ruby, Go, and PHP. While it maintains CFML compatibility through our `bx-compat-cfml` module, BoxLang introduces significant modern language features that require proper tooling support:

* Native class and interface declarations
* Bitwise operators with BoxLang-specific syntax (`b|`, `b&`, `b^`, `b~`, `b<>`, `b>>>`)
* Arrow functions (`=>`) and lambda functions (`->`)
* Strict equality operators (`===`, `!==`)
* Modern keywords: `assert`, `final`, `package`, `castas`
* Safe navigation operator (`?.`)
* Elvis operator (`?:`)
* Java Interop and Types
* Exception Marking
* So much more

Dual-Syntax Architecture {#h2-1-dual-syntax-architecture}
---------------------------------------------------------

BoxLang supports two complementary syntax modes, and our plugin provides complete support for both:

### 1. BoxLang Script (`.bx`, `.bxs`) {#h3-2-1-boxlang-script-bx-bxs}

Pure script syntax designed for classes, components, and business logic:

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 * Modern BoxLang class with enterprise features
 */
@Component
@Transactional
class UserService {

    property String username;
    property Array roles;

    public function init( required String username ) {
        this.username = username;
        this.roles = [];
        return this;
    }

    /**
     * Arrow function for concise returns
     */
    public function getProfile() =&gt; {
        return {
            username: this.username,
            roles: this.roles,
            isAdmin: this.hasRole( "admin" )
        };
    }

    /**
     * Lambda function for high-performance filtering
     */
    public function filterActive( required Array users ) {
        return users.filter( ( user ) -&gt; user.active === true );
    }

    /**
     * Bitwise operations for permission flags
     */
    public function hasPermission( numeric userFlags, numeric requiredFlag ) {
        return ( userFlags b&amp; requiredFlag ) === requiredFlag;
    }

    /**
     * Safe navigation with elvis operator
     */
    public function getEmail() {
        return this.user?.email ?: "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b3dddc9ed6ded2dadff3d6cbd2dec3dfd69dd0dcde">[email&nbsp;protected]</a>";
    }
}</pre>

2. BoxLang Templates (`.bxm`) {#h2-3-2-boxlang-templates-bxm}
-------------------------------------------------------------

Markup-based syntax for views, layouts, and content generation:

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;!--- User Dashboard Template ---&gt;
&lt;bx:output&gt;
    &lt;!DOCTYPE html&gt;
    &lt;html lang="en"&gt;
    &lt;head&gt;
        &lt;title&gt;#variables.pageTitle#&lt;/title&gt;
        &lt;meta charset="UTF-8"&gt;
    &lt;/head&gt;
    &lt;body&gt;
        &lt;h1&gt;Welcome, #user.getName()#!&lt;/h1&gt;

        &lt;bx:if condition="user.isAdmin()"&gt;
            &lt;div class="admin-panel"&gt;
                &lt;h2&gt;Admin Controls&lt;/h2&gt;
                &lt;p&gt;You have elevated privileges.&lt;/p&gt;
            &lt;/div&gt;
        &lt;bx:elseif condition="user.isPremium()"&gt;
            &lt;div class="premium-badge"&gt;Premium Member&lt;/div&gt;
        &lt;bx:else&gt;
            &lt;div class="upgrade-prompt"&gt;
                &lt;a href="/upgrade"&gt;Upgrade to Premium&lt;/a&gt;
            &lt;/div&gt;
        &lt;/bx:if&gt;

        &lt;bx:for array="#recentActivities#" index="i" item="activity"&gt;
            &lt;div class="activity-##i##"&gt;
                &lt;span class="timestamp"&gt;#activity.date#&lt;/span&gt;
                &lt;p&gt;#activity.description#&lt;/p&gt;
            &lt;/div&gt;
        &lt;/bx:for&gt;
    &lt;/body&gt;
    &lt;/html&gt;
&lt;/bx:output&gt;

&lt;bx:script&gt;
    // Embedded script with full syntax highlighting
    function loadActivities() {
        return queryExecute( 
            "SELECT * FROM activities WHERE userId = :userId ORDER BY created DESC",
            { userId: user.getId() }
        );
    }

    // Lambda function for data transformation
    var formatActivities = ( activities ) -&gt; {
        return activities.map( ( a ) -&gt; {
            date: dateFormat( a.created, "medium" ),
            description: a.description
        } );
    };
&lt;/bx:script&gt;</pre>

Feature Highlights {#h2-4-feature-highlights}
---------------------------------------------

### Comprehensive Language Support {#h3-5-comprehensive-language-support}

The plugin recognizes and highlights:

* Control Flow: if, else, elseif, for, while, do, switch, case, try, catch, finally
* Type System: any, array, boolean, numeric, string, struct, query, date, function, closure, lambda, class
* Modifiers: public, private, remote, package, static, final, abstract, required
* Operators: All standard operators plus BoxLang-specific bitwise, safe navigation, and elvis operators
* Built-In Functions: Comprehensive recognition of 200+ BoxLang BIFs organized by category:
  * String manipulation (ArrayAppend, ListAppend, etc.)
  * Math operations (Abs, Round, etc.)
  * Date/time handling (DateFormat, CreateDateTime, etc.)
  * Struct operations (StructNew, StructKeyExists, etc.)
  * Array functions (ArrayMap, ArrayFilter, ArrayReduce, etc.)
  * Query operations (QueryNew, QueryAddRow, etc.)

### HTML Integration in Templates {#h3-6-html-integration-in-templates}

Unlike generic XML syntax highlighting, our `.bxm` template support includes intelligent HTML awareness:

**- Special tag recognition:** Distinct colors for html, head, body, script, style, link  
**- DOCTYPE declarations:** Proper highlighting for document types  
**- HTML comments:** Full support for alongside BoxLang comments   
**- Attribute handling:** Smart parsing of HTML and bx: tag attributes

### Expression Interpolation {#h3-7-expression-interpolation}

BoxLang uses `#expression#` for string interpolation and dynamic output. The plugin correctly highlights:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var message = "Hello, #user.name#!";
var calculation = "Result: #2 + 2#";
var nested = "Status: #user.isActive() ? 'Active' : 'Inactive'#";</pre>

In templates:

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;div class="user-##userId##" data-role="#user.role#"&gt;
    #user.displayName#
&lt;/div&gt;</pre>

### Code Folding Support {#h3-8-code-folding-support}

Automatic folding for major code structures:

* Classes and interfaces
* Functions and closures
* Control structures (`if`, `for`, `while`, `switch`, `try`)
* Tag regions in templates
* Comment blocks

Folding commands:

* `za` - Toggle fold under cursor
* `zR` - Open all folds
* `zM` - Close all folds
* `zo` - Open fold under cursor
* `zc` - Close fold under cursor

Installation {#h2-9-installation}
---------------------------------

### Lazy.nvim (Recommended for NeoVim) {#h3-10-lazy-nvim-recommended-for-neovim}

Add to your plugin configuration (e.g., `lua/plugins/boxlang.lua`):

<pre class="EnlighterJSRAW" data-enlighter-language="java">return {
  {
    "ortus-boxlang/vim-boxlang",
    ft = { "boxlang", "boxlangTemplate" }, -- Lazy load on filetype
    init = function()
      -- Optional: Custom configuration
    end,
  }
}</pre>

### vim-plug {#h3-11-vim-plug}

Add to your `.vimrc` or `init.vim`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Plug 'ortus-boxlang/vim-boxlang'</pre>

Then run `:PlugInstall`

### Vundle {#h3-12-vundle}

Add to your `.vimrc`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Plugin 'ortus-boxlang/vim-boxlang'</pre>

Then run `:PluginInstall`

### Manual Installation {#h3-13-manual-installation}

<pre class="EnlighterJSRAW" data-enlighter-language="java"># Clone the repository
git clone https://github.com/ortus-boxlang/vim-boxlang.git

# Copy to your vim runtime directory
# For Vim:
cp -r vim-boxlang/syntax ~/.vim/
cp -r vim-boxlang/ftdetect ~/.vim/

# For NeoVim:
# Linux/macOS:
cp -r vim-boxlang/syntax ~/.config/nvim/
cp -r vim-boxlang/ftdetect ~/.config/nvim/

# Windows:
cp -r vim-boxlang/syntax ~/AppData/Local/nvim/
cp -r vim-boxlang/ftdetect ~/AppData/Local/nvim/</pre>

File Extension Detection {#h2-14-file-extension-detection}
----------------------------------------------------------

The plugin automatically detects BoxLang files based on extensions:

* `.bx` → BoxLang script class/component files
* `.bxs` → BoxLang executable script files
* `.bxm` → BoxLang template/markup files

If automatic detection fails, manually set the filetype:

<pre class="EnlighterJSRAW" data-enlighter-language="java">" For script files
:setfiletype boxlang

" For template files
:setfiletype boxlangTemplate</pre>

Or add a modeline to your file:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// vim: set filetype=boxlang:</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;!--- vim: set filetype=boxlangTemplate: ---&gt;</pre>

Customization {#h2-15-customization}
------------------------------------

Personalize syntax colors by adding to your `.vimrc` or `init.vim`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">" BoxLang brand colors (#00FF78, #00DBFF)
hi boxlangKeyword ctermfg=cyan guifg=#00DBFF
hi boxlangOperator ctermfg=green guifg=#00FF78

" String colors
hi boxlangStringSingle ctermfg=green guifg=#00FF78
hi boxlangStringDouble ctermfg=green guifg=#00FF78

" Make bitwise operators stand out
hi boxlangBitwiseOp ctermfg=magenta guifg=#ff00ff gui=bold

" Function names
hi boxlangFunction ctermfg=yellow guifg=#FFF500

" Comments
hi boxlangComment ctermfg=darkgray guifg=#666666</pre>

Advanced Configuration {#h2-16-advanced-configuration}
------------------------------------------------------

### Enable Folding {#h3-17-enable-folding}

<pre class="EnlighterJSRAW" data-enlighter-language="java">" Add to .vimrc or init.vim
set foldenable
set foldmethod=syntax
set foldlevelstart=10</pre>

### BoxLang-Specific Keybindings {#h3-18-boxlang-specific-keybindings}

<pre class="EnlighterJSRAW" data-enlighter-language="java">" Quick function navigation
autocmd FileType boxlang,boxlangTemplate nnoremap &lt;buffer&gt; ]f /function&lt;CR&gt;
autocmd FileType boxlang,boxlangTemplate nnoremap &lt;buffer&gt; [f ?function&lt;CR&gt;

" Toggle between script and template
function! ToggleBoxLangSyntax()
    if &amp;filetype == 'boxlang'
        setfiletype boxlangTemplate
    else
        setfiletype boxlang
    endif
endfunction
nnoremap &lt;leader&gt;bt :call ToggleBoxLangSyntax()&lt;CR&gt;</pre>

What's Next? {#h2-19-what-s-next}
---------------------------------

This release establishes the foundation for BoxLang's Vim/NeoVim ecosystem. Future enhancements include:

* **LSP Integration**: Language Server Protocol support for autocomplete, go-to-definition, and refactoring
* **Snippet Library**: Common BoxLang patterns and templates
* **Debugger Integration**: DAP (Debug Adapter Protocol) support
* **Enhanced Folding**: Context-aware folding for complex structures
* **Semantic Highlighting**: Advanced token-based coloring using TreeSitter

Community \& Support {#h2-20-community-support}
-----------------------------------------------

The BoxLang NeoVim plugin is professionally maintained by Ortus Solutions with community contributions welcome:

* **GitHub Repository** : <https://github.com/ortus-boxlang/vim-boxlang>
* **Documentation** : <https://boxlang.ortusbooks.com/getting-started/ide-tooling/boxlang-neovim-plugin>
* **Issues \& Feature Requests**: GitHub Issues
* **Community Forums** : <https://community.ortussolutions.com>

Try BoxLang Today {#h2-21-try-boxlang-today}
--------------------------------------------

If you haven't explored BoxLang yet, now is the perfect time:

* **Download** : <https://boxlang.io/download>
* **Documentation** : <https://boxlang.ortusbooks.com>
* **Quick Start** : <https://boxlang.ortusbooks.com/getting-started/quick-start>
* **Examples** : <https://github.com/ortus-boxlang/boxlang-examples>  
  BoxLang combines the rapid development capabilities of dynamic languages with the performance and reliability of the JVM. Whether you're building web applications, serverless functions, CLI tools, or enterprise systems, BoxLang provides the modern syntax and features you need.

Conclusion {#h2-22-conclusion}
------------------------------

The BoxLang NeoVim plugin delivers professional-grade syntax highlighting specifically designed for BoxLang's modern feature set. With comprehensive language support, dual-syntax architecture, and intelligent HTML integration, it provides the foundation for productive BoxLang development in Vim and NeoVim environments.

Install the plugin today and experience BoxLang development with proper tooling support. Your feedback and contributions help shape the future of BoxLang developer tooling.

Happy coding! 🚀
