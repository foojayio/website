---
title: "AI Applications with LangChain4j Guardrails and Spring Boot"
slug: "building-robust-ai-applications-with-langchain4j-guardrails-and-spring-boot"
date: "2025-07-29T11:16:31+00:00"
lastmod: "2025-08-11T07:12:42+00:00"
description: "Learn how to build secure AI applications using LangChain4j guardrails in Spring Boot. Implement input/output validation, prevent prompt injection & more."
canonical: "https://bazlur.ca/2025/06/21/building-robust-ai-applications-with-langchain4j-guardrails-and-spring-boot/"
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2025/06/u6131494527_1._Shield__AI_Brain_Concept__A_modern_minimalist__c6366e07-45bb-4d60-8f31-a4380e8e1bd8_0.png"
categories:
  - "AI"
  - "Java"
  - "LangChain4j"
  - "Machine Learning"
tags:
related_posts:
  - "ai-powered-chat-application-using-ibm-watsonx-ai-and-spring-ai"
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "ai-driven-testing-best-practices"
  - "azul-and-jetbrains-collaborate-to-enhance-runtime-performance-for-kotlin-workloads"
enlighterjs: true
frozen: false
---

**As AI applications become increasingly complex, ensuring that language models behave predictably and safely is paramount. LangChain4j's guardrails feature provides a powerful framework for validating both the inputs and outputs of your AI services.**

This article demonstrates how to implement comprehensive guardrails in a Spring Boot application, with practical examples that you can adapt to your use cases.
> 📦 **Complete source code available at** : [github.com/rokon12/guardrails-demo](https://github.com/rokon12/guardrails-demo)

Understanding LangChain4j Guardrails {#h2-0-understanding-langchain4j-guardrails}
---------------------------------------------------------------------------------

In LangChain4j, guardrails are validation mechanisms that operate exclusively on AI Services, the framework's high-level abstraction for interacting with language models. Unlike simple validators, guardrails provide sophisticated control over the entire AI interaction lifecycle.

1. **Input Guardrails** : Act as gatekeepers, validating user input before it reaches the LLM
   1. Prevent prompt injection attacks
   2. Filter inappropriate content
   3. Enforce business rules
   4. Sanitize and normalize input
2. **Output Guardrails** : Act as quality controllers, validating and potentially correcting LLM responses
   1. Ensure a professional tone
   2. Detect hallucinations
   3. Validate response format
   4. Enforce compliance requirements

This dual-layer approach ensures that your AI applications remain safe, compliant, and aligned with business requirements.

Setting Up a Spring Boot Project with LangChain4j {#h2-1-setting-up-a-spring-boot-project-with-langchain4j}
-----------------------------------------------------------------------------------------------------------

Let's start by creating a Spring Boot application with the necessary dependencies. You can use [Spring Initializr](https://start.spring.io/) to bootstrap your project or create it directly in your IDE (IntelliJ IDEA, Eclipse, or VS Code).
> 🚀 **Quick Start with Spring Initializr:**
>
> 1. Go to [start.spring.io](https://start.spring.io/)
> 2. Choose: Maven/Gradle, Java 21+, Spring Boot 3.x
> 3. Add dependencies: Spring Web
> 4. Generate and import into your IDE
> 5. Add LangChain4j dependencies manually to your `pom.xml` or `build.gradle`
>
> 

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
    &lt;!-- Spring Boot Essentials --&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
    &lt;/dependency&gt;

    &lt;dependency&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-validation&lt;/artifactId&gt;
    &lt;/dependency&gt;

    &lt;!-- LangChain4j Core --&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;dev.langchain4j&lt;/groupId&gt;
        &lt;artifactId&gt;langchain4j&lt;/artifactId&gt;
        &lt;version&gt;1.1.0&lt;/version&gt; &lt;!-- ⚠️ Always check for the latest stable version --&gt;
    &lt;/dependency&gt;

    &lt;!-- LangChain4j OpenAI Integration --&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;dev.langchain4j&lt;/groupId&gt;
        &lt;artifactId&gt;langchain4j-open-ai&lt;/artifactId&gt;
        &lt;version&gt;1.1.0&lt;/version&gt;
    &lt;/dependency&gt;

    &lt;!-- Testing Support --&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;dev.langchain4j&lt;/groupId&gt;
        &lt;artifactId&gt;langchain4j-test&lt;/artifactId&gt;
        &lt;version&gt;1.1.0&lt;/version&gt;
        &lt;scope&gt;test&lt;/scope&gt; &lt;!-- 💡 Keep test dependencies scoped appropriately --&gt;
    &lt;/dependency&gt;

    &lt;!-- Metrics and Monitoring --&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

Configure your application:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># application.yml
langchain4j:
  open-ai:
    chat-model:
      api-key: ${OPENAI_API_KEY} # 🔐 NEVER hardcode API keys - use environment variables
      model-name: gpt-4 # 💡 Consider cost vs performance when choosing models
      temperature: 0.7 # 🎲 Balance between creativity (1.0) and consistency (0.0)
      max-tokens: 1000 # 💰 Control costs by limiting response length
      timeout: 30s # ⏱️ Prevent hanging requests
      log-requests: true # 🔍 Enable for debugging, disable in production for performance
      log-responses: true

# Application-specific settings
app:
  guardrails:
    input:
      max-length: 1000 # 📏 Prevent resource exhaustion from large inputs
      rate-limit:
        enabled: true
        max-requests-per-minute: 10 # 🛡️ Protect against abuse and control costs
    output:
      max-retries: 3 # 🔄 Balance between reliability and latency</pre>

Implementing Input Guardrails {#h2-2-implementing-input-guardrails}
-------------------------------------------------------------------

Input guardrails shield your application from malicious, inappropriate, or out-of-scope user inputs. Here are several practical examples.

### Content Safety Input Guardrail {#h3-3-content-safety-input-guardrail}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Component
public class ContentSafetyInputGuardrail implements InputGuardrail {

    // 🚫 Customize this list based on your application's domain and risk profile
    private static final List&lt;String&gt; PROHIBITED_WORDS = List.of(
            "hack", "exploit", "bypass", "illegal", "fraud", "crack", "breach",
            "penetrate", "malware", "virus", "trojan", "backdoor", "phishing",
            "spam", "scam", "steal", "theft", "identity", "password", "credential"
    );

    // 🎭 Detect obfuscated threats using regex patterns
    private static final List&lt;Pattern&gt; THREAT_PATTERNS = List.of(
            Pattern.compile("h[4@]ck", Pattern.CASE_INSENSITIVE), // Catches "h4ck", "h@ck"
            Pattern.compile("cr[4@]ck", Pattern.CASE_INSENSITIVE),
            Pattern.compile("expl[0o]it", Pattern.CASE_INSENSITIVE),
            Pattern.compile("byp[4@]ss", Pattern.CASE_INSENSITIVE),
            // 🎯 This pattern catches instruction-style prompts for malicious activities
            Pattern.compile("[\\w\\s]*(?:how\\s+to|teach\\s+me|show\\s+me)\\s+(?:hack|exploit|bypass)", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String originalText = userMessage.singleText();
        String text = originalText.toLowerCase();

        // 📏 Length validation should be your first check for performance
        if (originalText.length() &gt; 1000) {
            return failure("Your message is too long. Please keep it under 1000 characters.");
        }

        // 🔍 Check for prohibited words
        for (String word : PROHIBITED_WORDS) {
            if (text.contains(word)) {
                // ⚠️ Be careful not to reveal too much about your security measures
                return failure("Your message contains prohibited content related to security threats.");
            }
        }

        // 🎭 Check for obfuscated patterns
        for (Pattern pattern : THREAT_PATTERNS) {
            if (pattern.matcher(originalText).find()) {
                return failure("Your message contains potentially harmful content patterns.");
            }
        }

        return success();
    }
}</pre>

### **Smart Context-Aware Guardrail** {#h3-4-smart-context-aware-guardrail}

This guardrail uses conversation history to make intelligent decisions:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Component
@Slf4j
public class ContextAwareInputGuardrail implements InputGuardrail {

    private static final int MAX_SIMILAR_QUESTIONS = 3;
    private static final double SIMILARITY_THRESHOLD = 0.8; // 📊 Adjust based on your tolerance

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        ChatMemory memory = request.memory();
        UserMessage currentMessage = request.userMessage();

        // 💡 Always handle null cases gracefully
        if (memory == null || memory.messages().isEmpty()) {
            return success();
        }

        // Check for repetitive questions
        List&lt;String&gt; previousQuestions = extractUserQuestions(memory);
        String currentQuestion = currentMessage.singleText();

        long similarQuestions = previousQuestions.stream()
            .filter(q -&gt; calculateSimilarity(q, currentQuestion) &gt; SIMILARITY_THRESHOLD)
            .count();

        if (similarQuestions &gt;= MAX_SIMILAR_QUESTIONS) {
            // 📝 Log suspicious behavior for security monitoring
            log.info("User asking repetitive questions: {}", currentQuestion);
            return failure("You've asked similar questions multiple times. Please try a different topic or rephrase your question.");
        }

        // Check conversation velocity (potential abuse)
        if (isConversationTooFast(memory)) {
            return failure("Please slow down. You're sending messages too quickly.");
        }

        return success();
    }

    private List&lt;String&gt; extractUserQuestions(ChatMemory memory) {
        return memory.messages().stream()
            .filter(msg -&gt; msg instanceof UserMessage) // 🎯 Type-safe filtering
            .map(ChatMessage::text)
            .collect(Collectors.toList());
    }

    private double calculateSimilarity(String s1, String s2) {
        // 🧮 Simple Jaccard similarity - in production, use more sophisticated methods
        // Consider: Levenshtein distance, cosine similarity, or semantic embeddings
        Set&lt;String&gt; set1 = new HashSet&lt;&gt;(Arrays.asList(s1.toLowerCase().split("\\s+")));
        Set&lt;String&gt; set2 = new HashSet&lt;&gt;(Arrays.asList(s2.toLowerCase().split("\\s+")));

        Set&lt;String&gt; intersection = new HashSet&lt;&gt;(set1);
        intersection.retainAll(set2);

        Set&lt;String&gt; union = new HashSet&lt;&gt;(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    private boolean isConversationTooFast(ChatMemory memory) {
        // ⏱️ TODO: Implement timestamp checking
        // Check if user is sending messages too quickly (potential spam)
        List&lt;ChatMessage&gt; recentMessages = memory.messages();
        if (recentMessages.size() &lt; 5) return false;

        // In a real implementation, you'd check timestamps
        // This is a simplified example
        return false;
    }
}</pre>

### **Intelligent Input Sanitizer** {#h3-5-intelligent-input-sanitizer}

This guardrail not only validates but also improves input quality:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Component
public class IntelligentInputSanitizerGuardrail implements InputGuardrail {

    // 🌐 Comprehensive URL pattern that handles most common URL formats
    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://[\\w\\-._~:/?#\\[\\]@!$&amp;'()*+,;=.]+", 
        Pattern.CASE_INSENSITIVE
    );

    // 📧 Standard email pattern - consider RFC 5322 for stricter validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", 
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String text = userMessage.singleText();

        // 🔒 Remove potential PII for privacy compliance (GDPR, CCPA)
        text = EMAIL_PATTERN.matcher(text).replaceAll("[EMAIL_REDACTED]");

        // 🔗 Clean URLs but keep them for context
        text = URL_PATTERN.matcher(text).replaceAll("[URL]");

        // 📝 Normalize whitespace for consistent processing
        text = text.replaceAll("\\s+", " ").trim();

        // 🛡️ Remove potentially harmful characters while preserving meaning
        // These characters could be used for injection attacks
        text = text.replaceAll("[&lt;&gt;{}\\[\\]|\\\\]", "");

        // ✂️ Smart truncation that preserves sentence structure
        if (text.length() &gt; 500) {
            text = smartTruncate(text, 500);
        }

        // 🔤 Fix common typos and normalize
        text = normalizeText(text);

        // ✅ Return the sanitized text, not just validation result
        return successWith(text);
    }

    private String smartTruncate(String text, int maxLength) {
        if (text.length() &lt;= maxLength) return text;

        // 📍 Try to cut at sentence boundary for better readability
        int lastPeriod = text.lastIndexOf('.', maxLength);
        if (lastPeriod &gt; maxLength * 0.8) { // 80% threshold ensures we don't cut too early
            return text.substring(0, lastPeriod + 1);
        }

        // 🔤 Otherwise, cut at word boundary
        int lastSpace = text.lastIndexOf(' ', maxLength);
        if (lastSpace &gt; maxLength * 0.8) {
            return text.substring(0, lastSpace) + "...";
        }

        // ✂️ Last resort: hard cut
        return text.substring(0, maxLength - 3) + "...";
    }

    private String normalizeText(String text) {
        // 🔧 Fix common issues
        text = text.replaceAll("\\bi\\s", "I ");  // i -&gt; I
        text = text.replaceAll("\\s+([.,!?])", "$1");  // Remove space before punctuation
        text = text.replaceAll("([.,!?])(\\w)", "$1 $2");  // Add space after punctuation

        return text;
    }
}</pre>

<br />

**ProTip:**Input sanitizers should be the last guardrail in your input chain. They clean and normalize input after all validation checks have passed.

Implementing Output Guardrails {#h2-6-implementing-output-guardrails}
---------------------------------------------------------------------

Output guardrails ensure that LLM responses meet your quality standards and business requirements.

### Professional Tone Output Guardrail {#h3-7-professional-tone-output-guardrail}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Component
public class ProfessionalToneOutputGuardrail implements OutputGuardrail {

    // 🚫 Phrases that damage professional credibility
    private static final List&lt;String&gt; UNPROFESSIONAL_PHRASES = List.of(
            "that's weird", "that's dumb", "whatever", "i don't know"
    );

    // ✨ Elements that enhance professional communication
    private static final List&lt;String&gt; REQUIRED_ELEMENTS = List.of(
            "thank you",
            "please",
            "happy to help"
    );

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String text = responseFromLLM.text().toLowerCase();

        // 🔍 Check for unprofessional language
        for (String unprofessionalPhrase : UNPROFESSIONAL_PHRASES) {
            if (text.contains(unprofessionalPhrase)) {
                // 🔄 Request reprompting with specific guidance
                return reprompt("Unprofessional tone detected",
                        "Please maintain a professional and helpful tone");
            }
        }

        // 📏 Enforce response length limits for better UX
        if (text.length() &gt; 1000) {
            return reprompt("Response too long",
                    "Please keep your response under 1000 characters.");
        }

        // 🎯 Ensure professional courtesy is present
        boolean hasCourtesy = REQUIRED_ELEMENTS.stream()
                .anyMatch(text::contains);
        if (!hasCourtesy) {
            return reprompt(
                    "Response lacks professional courtesy",
                    "Please include polite and helpful language in your response."
            );
        }

        return success();
    }
}</pre>

<br />

### Hallucination Detection Guardrail {#h3-8-hallucination-detection-guardrail}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package ca.bazlur.guardrailsdemo.guardrail;

import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailRequest;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import dev.langchain4j.rag.AugmentationResult;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HallucinationDetectionGuardrail implements OutputGuardrail {

    private static final Set&lt;String&gt; UNCERTAINTY_MARKERS = Set.of(
            "might be", "could be", "possibly", "potentially", "may",
            "i think", "i believe", "it seems", "apparently", "probably"
    );

    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest request) {
        var response = request.responseFromLLM();
        String responseText = response.aiMessage().text();

        AugmentationResult augmentationResult = null;
        try {
            augmentationResult = request.requestParams().augmentationResult();
        } catch (Exception e) {
            log.debug("No augmentation result available, treating as context-free validation");
        }

        // Check for overly confident claims without context
        if (augmentationResult == null || augmentationResult.contents().isEmpty()) {
            return checkForUnsubstantiatedClaims(responseText);
        }

        Set&lt;String&gt; responseFacts = extractKeyFacts(responseText);

        // Extract facts from the RAG context
        Set&lt;String&gt; contextFacts = new HashSet&lt;&gt;();
        augmentationResult.contents().forEach(content -&gt;
                contextFacts.addAll(extractKeyFacts(content.textSegment().text()))
        );

        // Check if response facts are grounded in context
        long ungroundedFacts = responseFacts.stream()
                .filter(fact -&gt; !isFactGrounded(fact, contextFacts))
                .count();

        double ungroundedRatio = responseFacts.isEmpty() ? 0 : (double) ungroundedFacts / responseFacts.size();

        if (ungroundedRatio &gt; 0.3) { // More than 30% ungrounded
            log.warn("High hallucination risk detected: {}% ungrounded facts", Math.round(ungroundedRatio * 100));
            return reprompt(
                    "Response contains potentially hallucinated information",
                    "Please base your response strictly on the provided context. Use phrases like 'According to the provided information...' when citing facts."
            );
        }

        return success();
    }

    private Set&lt;String&gt; extractKeyFacts(String text) {
        // Extract sentences that contain factual claims
        Set&lt;String&gt; facts = new HashSet&lt;&gt;();
        String[] sentences = text.split("[.!?]+");

        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() &gt; 10 &amp;&amp; containsFactualClaim(trimmed)) {
                facts.add(trimmed.toLowerCase());
            }
        }
        return facts;
    }

    private boolean containsFactualClaim(String sentence) {
        String lower = sentence.toLowerCase();
        // Look for patterns that indicate factual claims
        return lower.matches(".*(is|are|was|were|has|have|had|will|can|does|did).*") ||
                lower.matches(".*\\b\\d+\\b.*") || // Contains numbers
                lower.matches(".*(fact|data|study|research|report|according to).*");
    }

    private boolean isFactGrounded(String fact, Set&lt;String&gt; contextFacts) {
        // Check if the fact has significant overlap with context
        return contextFacts.stream()
                .anyMatch(contextFact -&gt; calculateSimilarity(fact, contextFact) &gt; 0.6);
    }

    private double calculateSimilarity(String fact1, String fact2) {
        Set&lt;String&gt; words1 = new HashSet&lt;&gt;(Arrays.asList(fact1.split("\\s+")));
        Set&lt;String&gt; words2 = new HashSet&lt;&gt;(Arrays.asList(fact2.split("\\s+")));

        Set&lt;String&gt; intersection = new HashSet&lt;&gt;(words1);
        intersection.retainAll(words2);

        Set&lt;String&gt; union = new HashSet&lt;&gt;(words1);
        union.addAll(words2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    private OutputGuardrailResult checkForUnsubstantiatedClaims(String text) {
        String lowerText = text.toLowerCase();

        // Check for absolute statements without uncertainty markers
        long absoluteStatements = Arrays.stream(text.split("[.!?]+"))
                .filter(s -&gt; s.matches(".*(always|never|all|every|none|must|definitely|certainly|guaranteed).*"))
                .count();

        // Check if there are uncertainty markers to balance absolute claims
        boolean hasUncertaintyMarkers = UNCERTAINTY_MARKERS.stream()
                .anyMatch(lowerText::contains);

        if (absoluteStatements &gt; 2 &amp;&amp; !hasUncertaintyMarkers) {
            return reprompt(
                    "Response contains unsubstantiated absolute claims",
                    "Please avoid absolute statements unless you're certain. Use qualified language when appropriate."
            );
        }

        return success();
    }
}</pre>

<br />

> **ProTip:** Hallucination detection can be computationally expensive. Consider using it selectively for critical responses or implementing caching for repeated content.

Testing Your Guardrails {#h2-9-testing-your-guardrails}
-------------------------------------------------------

Before integrating guardrails into your AI services, it's crucial to thoroughly test them. Here's a comprehensive test suite for the ContentSafetyInputGuardrail:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package ca.bazlur.guardrailsdemo.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.GuardrailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.langchain4j.test.guardrail.GuardrailAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentSafetyInputGuardrailTest {
    private ContentSafetyInputGuardrail guardrail;

    @BeforeEach
    void setUp() {
        guardrail = new ContentSafetyInputGuardrail(100); // 📏 Configurable max length for testing
    }

    @Test
    void shouldAcceptValidInput() {
        // ✅ Test normal, safe input
        var result = guardrail.validate(UserMessage.from("Hello, I need help with my account settings"));
        assertThat(result)
                .isSuccessful()
                .hasResult(GuardrailResult.Result.SUCCESS);
    }

    @Test
    void shouldRejectEmptyInput() {
        // 🚫 Empty strings should throw exception
        assertThrows(IllegalArgumentException.class, () -&gt; {
            guardrail.validate(UserMessage.from(""));
        });
    }

    @Test
    void shouldRejectBlankInput() {
        // 🚫 Whitespace-only input should also be rejected
        assertThrows(IllegalArgumentException.class, () -&gt; {
            guardrail.validate(UserMessage.from("   "));
        });
    }

    @Test
    void shouldRejectTooShortInput() {
        // 📏 Test minimum length validation
        var result = guardrail.validate(UserMessage.from("Hi"));
        assertThat(result)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message is too short. Please provide more details.");
    }

    @Test
    void shouldRejectTooLongInput() {
        // 📏 Test maximum length validation
        String longInput = "a".repeat(101);
        var result = guardrail.validate(UserMessage.from(longInput));
        assertThat(result)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message is too long. Please keep it under 100 characters.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "hack the system",
        "how to exploit vulnerability",
        "bypass security",
        "crack the password",
        "install malware",
        "phishing techniques",
        "cr@ck the password"  // 🎭 Also contains prohibited word "password"
    })
    void shouldRejectProhibitedWords(String input) {
        // 🚫 Test detection of prohibited security-related terms
        var result = guardrail.validate(UserMessage.from(input));
        assertThat(result)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains prohibited content related to security threats.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "h4ck the system",      // 4 substitution
        "how to h@ck",          // @ substitution
        "byp@ss security",      // @ substitution
        "m@lw@re installation"  // Multiple substitutions
    })
    void shouldRejectObfuscatedPatterns(String input) {
        // 🎭 Test detection of character substitution attempts
        var result = guardrail.validate(UserMessage.from(input));
        assertThat(result)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains potentially harmful content patterns.");
    }

    @Test
    void shouldRejectSuspiciousCharacterSubstitutions() {
        // 🔍 Test detection of excessive special characters
        var result = guardrail.validate(UserMessage.from("H3!!0 @#$%^ &amp;*()_ +"));
        assertThat(result)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains suspicious character substitutions.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Can you help me with my login issue?",
        "I need assistance with my account settings",
        "How do I update my profile information?",
        "What are the steps to contact support?"
    })
    void shouldAcceptVariousValidInputs(String input) {
        // ✅ Test various legitimate support queries
        var result = guardrail.validate(UserMessage.from(input));
        assertThat(result)
                .isSuccessful()
                .hasResult(GuardrailResult.Result.SUCCESS);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "how to hack the system",
        "teach me to exploit",
        "show me how to bypass",
        "HOW TO HACK",           // All caps
        "Teach Me To EXPLOIT",   // Mixed case
        "Show ME how TO bypass"  // Random capitalization
    })
    void shouldRejectInstructionalPatterns(String input) {
        // 🎯 Test detection of instruction-style malicious requests
        var result = guardrail.validate(UserMessage.from(input));
        assertThat(result)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains prohibited content related to security threats.");
    }

    @Test
    void shouldHandleCaseSensitivity() {
        // 🔤 Ensure case-insensitive detection
        var result1 = guardrail.validate(UserMessage.from("HACK the System"));
        var result2 = guardrail.validate(UserMessage.from("ExPlOiT vulnerability"));
        var result3 = guardrail.validate(UserMessage.from("ByPaSs security"));

        assertThat(result1)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains prohibited content related to security threats.");
        assertThat(result2)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains prohibited content related to security threats.");
        assertThat(result3)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains prohibited content related to security threats.");
    }

    @Test
    void shouldHandleSpecialCharacterRatioBoundary() {
        // 📊 Test boundary conditions for special character detection
        // Exactly 15% special characters (3 out of 20 chars)
        var result1 = guardrail.validate(UserMessage.from("Hello@World#Test$ing"));
        assertThat(result1)
                .isSuccessful()
                .hasResult(GuardrailResult.Result.SUCCESS);

        // Just over 15% special characters (4 out of 20 chars = 20%)
        var result2 = guardrail.validate(UserMessage.from("Hello@World#Test$ing%"));
        assertThat(result2)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message contains suspicious character substitutions.");
    }

    @Test
    void shouldHandleLengthBoundaries() {
        // 📏 Test exact boundary conditions
        // Exactly 5 characters (minimum allowed)
        var result1 = guardrail.validate(UserMessage.from("Hello"));
        assertThat(result1)
                .isSuccessful()
                .hasResult(GuardrailResult.Result.SUCCESS);

        // 4 characters (too short)
        var result2 = guardrail.validate(UserMessage.from("Help"));
        assertThat(result2)
                .hasFailures()
                .hasResult(GuardrailResult.Result.FAILURE)
                .hasSingleFailureWithMessage("Your message is too short. Please provide more details.");

        // Exactly max length
        var result3 = guardrail.validate(UserMessage.from("a".repeat(100)));
        assertThat(result3)
                .isSuccessful()
                .hasResult(GuardrailResult.Result.SUCCESS);
    }
}</pre>

<br />

> 💡 **Testing Best Practices for Guardrails:**
>
> * Test boundary conditions (minimum/maximum values)
> * Use parameterized tests for similar scenarios
> * Test both positive and negative cases
> * Verify exact error messages for better debugging
> * Test case sensitivity and special character handling
> * Use the `GuardrailAssertions` utility for cleaner test code

Creating AI Services with Guardrails {#h2-10-creating-ai-services-with-guardrails}
----------------------------------------------------------------------------------

Now let's combine our guardrails into comprehensive AI services.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Component
public class ProfessionalToneOutputGuardrail implements OutputGuardrail {

    // 🚫 Phrases that damage professional credibility
    private static final List&lt;String&gt; UNPROFESSIONAL_PHRASES = List.of(
            "that's weird", "that's dumb", "whatever", "i don't know"
    );

    // ✨ Elements that enhance professional communication
    private static final List&lt;String&gt; REQUIRED_ELEMENTS = List.of(
            "thank you",
            "please",
            "happy to help"
    );

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String text = responseFromLLM.text().toLowerCase();

        // 🔍 Check for unprofessional language
        for (String unprofessionalPhrase : UNPROFESSIONAL_PHRASES) {
            if (text.contains(unprofessionalPhrase)) {
                // 🔄 Request reprompting with specific guidance
                return reprompt("Unprofessional tone detected",
                        "Please maintain a professional and helpful tone");
            }
        }

        // 📏 Enforce response length limits for better UX
        if (text.length() &gt; 1000) {
            return reprompt("Response too long",
                    "Please keep your response under 1000 characters.");
        }

        // 🎯 Ensure professional courtesy is present
        boolean hasCourtesy = REQUIRED_ELEMENTS.stream()
                .anyMatch(text::contains);
        if (!hasCourtesy) {
            return reprompt(
                    "Response lacks professional courtesy",
                    "Please include polite and helpful language in your response."
            );
        }

        return success();
    }
}</pre>

<br />

### **Rest endpoint** {#h3-11-rest-endpoint}

Now that we have everything set up, let's create our REST endpoint so that we can invoke it:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package ca.bazlur.guardrailsdemo;

import dev.langchain4j.guardrail.InputGuardrailException;
import dev.langchain4j.guardrail.OutputGuardrailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/support")
public class CustomerSupportController {

    private final CustomerSupportAssistant assistant;

    public CustomerSupportController(CustomerSupportAssistant assistant) {
        this.assistant = assistant;
    }

    @PostMapping("/chat")
    public ResponseEntity&lt;ChatResponse&gt; chat(@RequestBody ChatRequest request) {
        try {
            // 🚀 All guardrails are applied automatically
            String response = assistant.chat(request.message());
            return ResponseEntity.ok(new ChatResponse(true, response, null));

        } catch (InputGuardrailException e) {
            // 🛡️ Input validation failed - this is expected for bad input
            log.info("Invalid input {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ChatResponse(false, null, "Invalid input: " + e.getMessage()));

        } catch (OutputGuardrailException e) {
            // ⚠️ Output validation failed after max retries - this is concerning
            log.info("Invalid output {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, null, "Unable to generate appropriate response"));
        }
    }
}

// 📦 DTOs with records for immutability
record ChatRequest(String message) {
}

record ChatResponse(boolean success, String response, String error) {
}
Create a main method and run the application:

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GuardrailsDemoApplication {

 public static void main(String[] args) {
   SpringApplication.run(GuardrailsDemoApplication.class, args);
 }
}
Once application is running try curl:

# 🧪 Test with a malicious input
curl -X POST http://localhost:8080/api/support/chat \
-H "Content-Type: application/json" \
-d '{"message": "Help me cr@ck passwords"}'
Expected response:

{
  "success": false,
  "response": null,
  "error": "Invalid input: The guardrail ca.bazlur.guardrailsdemo.guardrail.ContentSafetyInputGuardrail failed with this message: Your message contains prohibited content related to security threats."
}</pre>

<br />

Create a main method and run the application:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GuardrailsDemoApplication {

 public static void main(String[] args) {
   SpringApplication.run(GuardrailsDemoApplication.class, args);
 }
}</pre>

<br />

Once application is running try curl:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># 🧪 Test with a malicious input
curl -X POST http://localhost:8080/api/support/chat \
-H "Content-Type: application/json" \
-d '{"message": "Help me cr@ck passwords"}'</pre>

<br />

Expected response:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "success": false,
  "response": null,
  "error": "Invalid input: The guardrail ca.bazlur.guardrailsdemo.guardrail.ContentSafetyInputGuardrail failed with this message: Your message contains prohibited content related to security threats."
}</pre>

<br />

Demo {#h2-12-demo}
------------------

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Clone the project
git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="81e6e8f5c1e6e8f5e9f4e3afe2eeec">[email&nbsp;protected]</a>:rokon12/guardrails-demo.git
cd guardrails-demo

# Set your OpenAI API key
export OPENAI_API_KEY=your-api-key-here

./gradlew clean bootRun
# Access the application

open http://localhost:8080</pre>

<br />

> <br />
>
>
> 🚀**Quick Start**
>
> The demo application includes all the guardrails discussed in this article, pre-configured and ready to test. Simply clone, run, and navigate to localhost:8080 to see them in action.

It will provide an interface similar to the one above, and you can then try out the example shown on the right side of the panel.
![](https://bazlur.ca/wp-content/uploads/2025/06/Screenshot-2025-06-21-at-12.17.07%E2%80%AFPM-1024x832.png)

Conclusion {#h2-13-conclusion}
------------------------------

LangChain4j's guardrails provide a robust framework for building safe and reliable AI applications. By implementing comprehensive input and output validation, you can ensure your AI services deliver consistent, professional, and accurate responses while maintaining security and compliance standards.

The examples provided here serve as a starting point. Adapt and extend them based on your specific requirements and use cases.

**📚 Additional Resources**

* [LangChain4j Official Documentation](https://docs.langchain4j.dev/)
* [LangChain4j Guardrails](https://docs.langchain4j.dev/tutorials/guardrails)
* [Spring Boot AI Integration Guide](https://spring.io/guides/gs/spring-boot-ai/)
* [OWASP LLM Security Top 10](https://owasp.org/www-project-top-10-for-large-language-model-applications/)
* [AI Safety Best Practices](https://www.anthropic.com/safety)

Happy coding, and remember: with great AI power comes great responsibility! 🚀
