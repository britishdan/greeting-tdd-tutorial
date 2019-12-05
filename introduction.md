# TDD Tutorial
This tutorial will give you a glimps into how TDD is done at [Wix](https://www.wix.engineering/).  
The audience is assumed to have very little experience with TDD.  
The tutorial is best experienced hands-on. Follow the tutorial in your own IDE.

> One must learn by doing the thing; for though you think you know it, you have no certainty, until you try.
> —Sophocles

## Overview
At Wix we use an _outside-in_ approach. We create tests that trigger the system from the API.  
Testing from the _outside-in_ shows you how your users will see your API. Consider your tests as your first user.  
Testing from the _outside-in_ means that the test will go through all the layers of the system. It guarentees that the system is always integrated.  
> _Side note_  
> The alternative is the _inside-out_ approach, where each layer is developed separately and then integrated at the end.

### The 3 rules of TDD
Uncle Bob describes TDD with [3 simple rules](http://butunclebob.com/ArticleS.UncleBob.TheThreeRulesOfTdd):
> 1. You are not allowed to write any production code unless it is to make a failing unit test pass.
> 2. You are not allowed to write any more of a unit test than is sufficient to fail; and compilation failures are failures.
> 3. You are not allowed to write any more production code than is sufficient to pass the one failing unit test.

### The Red-Green-Refactor cycle
The cycle at the heart of TDD is:  
1. Write a test;
2. Write some code to get it working;
3. Refactor the code to be as simple an implementation of the tested features as possible;
4. Repeat.

![red-green-refactor](imgs/red-green-refactor.jpg)  
The red, green, refactor cycle, by [Nat Pryce](http://www.natpryce.com/articles.html).  

As is written in [GOOS](http://www.growing-object-oriented-software.com/):
> As we develop the system, we use TDD to give us feedback on the quality of both its implementation (“Does it work?”) and design (“Is it well structured?”). Developing test-first, we find we benefit twice from the effort.  
> Writing tests:
> - makes us clarify the acceptance criteria for the next piece of work — we have to ask ourselves how we can tell when we’re done (design);
> - encourages us to write loosely coupled components, so they can easily be tested in isolation and, at higher levels, combined together (design);
> - adds an executable description of what the code does (design); and,
> - adds to a complete regression suite (implementation);
whereas running tests:
> - detects errors while the context is fresh in our mind (implementation); and,
> - lets us know when we’ve done enough, discouraging “gold plating” and unnecessary features (design).
> This feedback cycle can be summed up by the Golden Rule of TDD:
> Never write new functionality without a failing test.

Now, let's get some hands-on experience.  
[Part 1](./part-1.md)