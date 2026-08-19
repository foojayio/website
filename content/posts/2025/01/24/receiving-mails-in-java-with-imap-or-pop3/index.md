---
title: "Receiving mails in Java with IMAP or POP3"
date: "2025-01-24T10:33:44+00:00"
lastmod: "2025-01-24T10:46:33+00:00"
description: "There is a lot of documentation for sending mails, but gathering information about the receiving and processing part is less easy. This example shows you how to receive mails in Java either using the IMAP or the POP3 protocol."
canonical: "https://jensknipper.de/blog/receiving-emails-in-java/"
authors:
  - "jens-knipper"
image: "mailusage.png"
categories:
  - "Java"
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-ways-to-contribute-to-openjdk"
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

**I was recently in need to write some small demo project which was receiving and processing mails. There is a lot of documentation for sending mails, but gathering information about the receiving and processing part is less easy.**

This example shows you how to receive mails in Java either using the IMAP or the POP3 protocol. The SSL encrypted variants IMAPS and POP3S are also supported. Received mails will be set to read, which means that you will only receive the latest ones. I will also give some hints about how to handle incoming mails concerning application security. Remember that you should always sanitize user inputs.

## Receiving mails

You need to use to the following import statement

```java
import javax.mail.*;
```

to be able to connect mail servers. The constructor of the client is straightforward. It is simply used to pass and store some values into fields, which are then used later on. We are interested in the protocol, host, port user and password. Valid values for the protocol are

```java
imap
```

,

```java
imaps
```

,

```java
pop3
```

and

```java
pop3s
```

.

```java
public final class MailReceiveClient {
    private final String protocol;
    private final String host;
    private final String port;
    private final String user;
    private final String password;

// ... constructor

}
```

### Connecting to the Server

We use the following code sample to connect to the mail server. First we have to create new properties and add values with the defined protocol, host and port. We use these properties to create a Session. With this session we can create a Store and connect to the server using the given username and password.

Afterwards we get the inbox folder and open it in read write mode. Inbox usually is the default folder. If you want to get a different one, just change this string. The folder is opened also in write mode to mark mails as read. We pass this folder into the

```java
getNewMails
```

method to receive the latest mails.

There is some wrapping of exceptions into runtime exceptions in the end. Folder and Store also have to be disconnected before returning.

```java
public List<Mail> receive() {
    Store emailStore = null;
    Folder emailFolder = null;

    Properties properties = new Properties();
    properties.put("mail.store.protocol", protocol);
    properties.put("mail." + protocol + ".host", host);
    properties.put("mail." + protocol + ".port", port);
    Session emailSession = Session.getInstance(properties);

    try {
        emailStore = emailSession.getStore();
        emailStore.connect(user, password);

        emailFolder = emailStore.getFolder("INBOX");
        emailFolder.open(Folder.READ_WRITE);

        return getNewMails(emailFolder);

    } catch (MessagingException e) {
        throw new RuntimeException(e);
    } finally {
        try {
            if (emailFolder != null && emailFolder.isOpen()) {
                emailFolder.close(false);
            }
            if (emailStore != null && emailStore.isConnected()) {
                emailStore.close();
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Processing Mails

The

```java
getNewMails
```

method gets all the messages in the folder. An if condition is used to only process unseen messages. Afterwards the message is set to seen.

Keep in mind that no data is being deleted. When processing big volumes of mails your folder gets bigger quite fast. You might want to delete messages, instead of marking them as read, to keep the mail processing short.

The code uses a for loop to avoid race conditions. In case there are multiple mail clients connected, mails are not processed multiple times.

A mapping to an internal class is needed, because an exception is thrown as soon as you try to access a message who's session has been closed. Another reason is the sanitizing of user inputs. You can read more about this in the next chapter.

```java
private List<Mail> getNewMails(Folder emailFolder) throws MessagingException {
    List<Mail> mails = new ArrayList<>();
    for (Message message : emailFolder.getMessages()) {
        if (!message.getFlags().contains(Flags.Flag.SEEN)) {
            message.setFlags(new Flags(Flags.Flag.SEEN), true);
            Mail mail = MailMapper.map(message, user);
            mails.add(mail);
        }
    }
    return mails;
}
```

The class can also be found on [GitHub](https://github.com/JensKnipper/greenmail-example/blob/main/src/main/java/de/jensknipper/greenmailexample/control/mail/receive/MailReceiveClient.java).

The error handling is just an example how it could be done. Depending on your needs you might want to use a more sophisticated one.

In the end a List of mails is returned. The mail class is just a simple data class containing information we extract from the messages on the mail server. For my minimal example I was only interested in the subject, the content, the sender and the recipient (even though there can be more than one recipient). You can extract even more informations from the messages. The documentation of the class can be found [here](https://javaee.github.io/javamail/docs/api/javax/mail/Message.html).

```java
public final class Mail {
    private final String subject;
    private final String content;
    private final String from;
    private final String recipient;

// ... constructor

// ... getters

}
```

You can read more about the mapping in the next chapter.

## Sanitizing received Mails

When you are dealing with user input (you could also count mails to this) you should always worry about code injections. The best way to prevent these is sanitizing the inputs you receive. You can obviously do this with mails as well.

OWASP provides a nice utility called [Encoder](https://owasp.org/www-project-java-encoder/) to do that.

I use a simple helper class with static methods to do the mapping. All the attributes received by mail will be encoded for displaying it in HTML. The encoder also brings methods for inserting your mail attributes into e.g. JavaScript or CSS.

We simply extract the things we are interested in from the message and sanitize the input using the encoder before creating a new mail object with the attributes.

```java
import org.owasp.encoder.Encode;

public final class MailMapper {

    private MailMapper() {}

    public static Mail map(Message message, String user) {
        String subject = Encode.forHtml(getSubject(message));
        String content = Encode.forHtml(getContent(message));
        String from = Encode.forHtml(getFrom(message));
        String recipient = Encode.forHtml(getRecipient(message, user));
        return new Mail(subject, content, from, recipient);
    }

    // ... methods

}
```

The class can also be found on [GitHub](https://github.com/JensKnipper/greenmail-example/blob/main/src/main/java/de/jensknipper/greenmailexample/control/mail/mapper/MailMapper.java).

## Usage

To get started simply fill in the constructor's parameters to connect to your mail server.  

An example for IMAP might look something like this:

```
new MailReceiveClient("imap", "localhost", "143", "user", "password");
```

And for POP3 like this:  

```
new MailReceiveClient("pop3", "localhost", "110", "user", "password");
```

You could also use Spring to create a bean using the

```java
@Component
```

annotation and set the constructor's parameters via properties using the

```java
@Value
```

annotation. This is the way I am doing it in my [demo project](https://github.com/JensKnipper/greenmail-example).

If you do not know how to easily start up a local (mock) mail server you can use GreenMail to do that. I have written an article how to [integrate it into you local environment](https://jensknipper.de/blog/greenmail-mock-mail-server-dev-setup/).

## Testing

There is quite some logic in this piece of code apart from the connection to the server. Things that you should test! I recently held a [presentation at a meetup about testing mails](https://jensknipper.de/blog/openvalue-meetup-greenmail-talk). You can use this resource to get started. Along to the repo I wrote a test for this class, which you can check out on [GitHub](https://github.com/JensKnipper/greenmail-example/blob/main/src/test/java/de/jensknipper/greenmailexample/control/mail/receive/MailReceiveClientTest.java).

As always full source code is available on [GitHub](https://github.com/JensKnipper/greenmail-example/blob/main/src/main/java/de/jensknipper/greenmailexample/control/mail/receive/MailReceiveClient.java).
