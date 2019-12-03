# TDD Tutorial
The purpose of this tutorial is to give you a glimps into how TDD is done at Wix.  
The audience is assumed to have very little experience with TDD.
### Overview
At Wix we use an _outside-in_ approach. We create tests that trigger the system from the API.  
Testing from the _outside-in_ shows you how your users will see your API. Consider your tests as your first user.  
Testing from the _outside-in_ means that the test will go through all the layers of the system. It guarentees that the system is always integrated.  
> _Side note_  
> The alternative is the _inside-out_ approach, where each layer is developed separately and then integrated at the end.
## Greeting server
Let's create a web server that greets people but also likes to take an afternoon nap.
### Requirements
1. Respond to a GET /greeting with “Hello”
2. Respond to a GET /greeting?name=Dalia with “Hello Dalia”
3. Respond to any GET /greeting with “I’m Sleeping” between 14:00-16:00 (UTC)
