---
title: "Unlocking Java Wisdom: A Conversation with Oracle ACE Simon Martinelli"
date: "2023-09-01T16:00:01+00:00"
lastmod: "2023-09-01T16:00:04+00:00"
description: "Explore insights from Simon Martinelli, Oracle ACE Associate, as he discusses his journey in Java development, the importance of simplicity in software architecture, and the value of mentoring."
authors:
  - "bazlur-rahman"
image: "Simon-Martinelli.png"
categories:
  - "Interviews"
tags:
related_posts:
  - "interview-with-a-java-champion-reflections-on-a-storied-career-and-insights-for-the-next-generation"
  - "journey-of-a-java-champion-bert-jan-schrijvers-path-to-mastery-and-community-leadership"
  - "cracking-code-and-conventions-an-exclusive-interview-with-nicolas-frankel"
  - "resilience-in-coding-miro-wengner-on-thriving-in-the-software-industry"
frozen: false
---

{{< img src="image-2.png" class="alignright size-full" width="460" height="460" >}}

In a recent insightful interview, Simon Martinelli, an Oracle ACE associate and veteran Java developer, shares his career experiences, software development philosophies, and views on mentoring. With over two decades in the industry, Martinelli offers a perspective that combines the wisdom of experience with a focus on simplicity and constant learning.

## Profiles:

* 🌍 Homepage:[martinelli.ch](https://martinelli.ch/)
* 👔 LinkedIn:[Simon Martinelli on LinkedIn](https://www.linkedin.com/in/simonmartinelli/?originalSubdomain=ch)
* 🐦 Twitter:[@simas_ch on Twitter](https://twitter.com/simas_ch)
* 📦 GitHub:[simasch on GitHub](https://github.com/simasch)

## Fun Fact:

> Before starting my IT career, I was working as a traffic controller at Swiss Railways.  

***Bazlur: First of all, congratulations, Simon, on your recent achievement of becoming the Oracle ACE associate. How are you feeling?***

Thank you. I feel great. It's an honour to be part of this group of experts.

***Bazlur: Tell us a bit about this program and what are the perks of becoming one. How is this different from the Java Champion program?***

Simon: The Oracle ACE program is a community program run by Oracle. It is designed to recognize and reward individuals who have demonstrated exceptional expertise and contributions in various Oracle-related technologies, such as databases, middleware, applications, and more.

Oracle ACEs are recognized as experts and are expected to actively contribute to the Oracle community through blogging, speaking at conferences, participating in user groups, and sharing their knowledge with others.

There is a points-based system, and you have to enter your contributions every year, which are then evaluated. Only if you reach the required number of points will you keep the Oracle ACE status.

The Java Champion program, on the other hand, is specifically focused on recognizing individuals who have made significant contributions to the Java ecosystem. Java Champions are individuals who are passionate about Java and related technologies and who actively contribute to the community by sharing knowledge, advocating for Java, and assisting in the development of the Java platform.

While there are similarities between the Oracle ACE and Java Champion programs, such as recognition, networking, and community involvement, there are also some differences:

Oracle ACE program covers a broader range of Oracle technologies, including databases, middleware, and applications, whereas the Java Champion program is solely focused on Java and related technologies.

The Oracle ACE program is associated with Oracle Corporation, while the Java Champion program is more community-driven and not tied to any specific company.

***Bazlur: Can you walk us through your career path and what led you to focus on Java? What are the key philosophies or principles that guide your approach to software development?***   

Simon: I started my career as a software developer in 1995 at Swiss Railways as a Cobol developer on IBM mainframe. I helped fix the year 2000 problem and then had the chance to move to the newly created Java department. Since 2009, I have been working as a freelance consultant for many companies, primarily in Switzerland but also in other European countries. I have also been a lecturer at two universities in Switzerland for 16 years.

I always try to create the simplest possible solution to suit the customer's needs in order to keep the development effort as low as possible and make maintenance as easy as possible. This is not so easy in times with an enormous variety of architectural styles and technologies.
> ***I always try to create the simplest possible solution to suit the customer's needs in order to keep the development effort as low as possible and make maintenance as easy as possible.***

It is also very important for me to constantly expand my know-how and to learn from others in order to then pass this know-how on to others.

***Bazlur: Your emphasis on simplicity in solutions resonates with many in the software industry. Can you share some specific examples where this philosophy has guided your work in Java development? How do you strike a balance between simplicity and the demands of modern, complex systems?***   

Simon: A modern complex system does not require a complicated, over-designed architecture. It's important always to choose the right tools that match the requirements. Most often, these are old boring technologies like SQL databases, message brokers and in the frontend Vaadin or any server-side-rendering web framework: this reduced operations overhead and costs for my customers.  
> ***A modern complex system does not require a complicated, over-designed architecture. It's important always to choose the right tools that match the requirements.***

**Bazlur: With the ever-changing landscape of technologies and frameworks, how do you decide which ones to incorporate into your Java projects? Can you describe any recent projects where you had to adapt to new technologies or methodologies?**

Simon: In my opinion, experience and knowing concepts are key. With the help of those, it's easier to evaluate new technologies and methodologies and choose the right ones. Very often, something new is just an old concept with a new fancy name.
> ***Very often, something new is just an old concept with a new fancy name.***

In a recent project, we had to decide which front-end technology to use. As a Vaadin fan, I wanted to go with Vaadin, but there were some requirements, like the avoidance of any server state, that made me rethink this choice. Finally, we evaluated Angular, React and Hilla with Lit, and we decided to use Hilla because, for Java developers, this was the front-end technology with the lowest entry barrier. Very often, an architectural decision is also driven by the team. So, check the knowledge and the experience of the team to find a matching solution.

**Bazlur: Collaboration and teamwork are essential in software development. How have you approached working with diverse teams, especially in a freelance consultant role? What challenges have you faced, and how have you overcome them?**

Simon: I am very open-minded and enjoy working with and learning from people with different experiences and opinions. But sometimes, it's difficult to find the right way to explain your ideas to people and convince them of your solution. Most of the time, I try to cut down on the arguing and create working examples so they can see and try how the solution might work.

On the other hand, it takes time to get to know the people in a new team and gain their trust.

**Bazlur: You've been involved in Java development for many years, witnessing its evolution. What do you think are the most significant changes in Java and its ecosystem over the years? How have these changes influenced your work and the industry at large?**

Simon: In my opinion, Java 5 and 8 had the biggest impact on how we use Java today. In Java 5, annotations were introduced that removed the need for XML configuration in Java EE and Spring Framework and allowed to have configuration close to the code (which, in fact, could also be a disadvantage). A more dramatic change was in Java 8 with lambdas and the Stream API. Especially the Stream API allows the program to be in a declarative way that leads to cleaner and more readable code.

**Bazlur: Mentoring and teaching seem to be important aspects of your career. How do you approach mentoring younger developers or students? Can you share any success stories or lessons learned from your time as a lecturer and mentor?**

Simon: Someone once said that you don't understand a thing until you can explain it to someone. That's my motivation behind mentoring and teaching. I have been teaching for 16 years, and my students always liked my courses. The most positive point is that they find my lessons very practical. For me, it's very important always to combine theory and practice. What I value even more is constructive feedback. This helps me to improve and not to stop becoming an even better teacher.
> ***Someone once said that you don't understand a thing until you can explain it to someone. That's my motivation behind mentoring and teaching.***

**Bazlur: They say that Swiss watches and Swiss trains are the epitomes of precision. As someone who started at Swiss Railways, do you apply the same level of Swiss precision to your Java code? Have you ever been late to a meeting and wished you could debug the train schedule like a piece of code?**   

Simon: I think it's in the nature of the Swiss to be very precise and thorough. Punctuality is also very important. We are already upset about a train that is 5 minutes late. But this doesn't mean that I'm never late for a meeting. To be honest, there have been so many meetings in my career it probably didn't matter if I was there or not.

The Swiss train schedule is way simpler than program code as we invented the "Taktfahrplan" (synchronized timetables), which means that the trains in a station always run at the same time every hour or every half an hour. You can just go to the train station and take the next train. Compared to a lot of program code, if seen in the last 27 years, this feels like a breeze.

**Bazlur: With your extensive experience in Java development and education, many aspiring developers would value your recommendations. Could you share a list of essential resources, books, or online platforms that have been instrumental in your career or that you often suggest to your students**?

Simon: I was very lucky to work with great people who taught me a lot. When I started my software development career in 1995, I was assigned a mentor who supported me during the first few months. So, my advice is to find such a mentor. Companies should set up such a mentoring program if they don't already have one.

When I started, there was no internet available, and I learned a lot from books. One book that I really liked was Distributed Systems by Andrew S. Tanenbaum. Another book that influenced my work years later is The Pragmatic Programmer by Andrew Hunt and David Thomas.

Nowadays, the sheer number of blogs and YouTube channels is overwhelming, and on the one hand, it's easy to find information, but on the other hand, it's very difficult to find the right information. This became even harder with AI. Therefore, it is very important to develop the skill to choose the right information from a large number of possibilities.

**Bazlur: Thank you for sharing your wisdom with us today. As we conclude, what parting advice would you offer to Java developers at different stages of their careers? How can they stay inspired and continue to grow in this ever-evolving field?**

Start writing and/or talking about what you do. Primarily, engaging in this process aids in your improvement as it requires a deep engagement with a subject. Additionally, your experience can serve to both assist and motivate others in their own progress.  
>
> ***Start writing and/or talking about what you do. Primarily, engaging in this process aids in your improvement as it requires a deep engagement with a subject.***

Another great way to grow your knowledge is a side project. Look for something that could be useful for the community, your family or friends. This side project will allow you to experiment with new technologies even if you have to deal with old stuff in your everyday job.

And finally - stay curious!

### Conclusion

The wisdom shared by Simon Martinelli is not just a reflection of his own journey but serves as guiding principles for software developers at all stages of their careers. Whether you're a seasoned developer or just starting out, his emphasis on simplicity in solutions, the importance of mentoring, and the value of ongoing education make for invaluable lessons. His pragmatic and down-to-earth insights provide a road map for navigating the complex, ever-evolving landscape of software development.
