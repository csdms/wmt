WMT-API: Functional Specification
=================================
Author: Eric Hutton
Last Updated: 2014-11-18

Overview
--------

WMT-API is a web api that allows people get information about numerical
models, connect the models to one another, and then run the models on a
remote cluster.

*This spec is not, by any stretch of the imagination, complete*. It will
100% change between now and a finished product.

This spec deals only with how a person might interact with the API. It does
not discuss any implementation details and certainly does not discuss any
of the models that can be run through the API.

Scenarios
---------

Although WMT-API is a web API that is really designed for use by a client
program, I'll outline some scenarios as though real people will be interacting
with the API because that could happen and it makes it sound more interesting.

*Scenario 1: Mark.*

Mark is a busy professor at a university in a quaint mountain town. He has
heard of a model called *zowie* and would like to run it. Unfortunately, his
university is too cheap to buy him a proper computer and so he can not
*zowie* on the meager resources available to him.

Non-Goals
---------

This version will not have the ability for a user to construct a model
blueprint. The API assumes that people using the API are able to construct
their own JSON-formatted blueprint.

URL-by-URL Specification
------------------------

/api/users
``````````

If someone wants information about WMT users, or wants to do user-y things
(like login/out) they send HTTP requests to URLs that start with */api/users*.

If a user sends a GET to */api/users*, the server constructs a collection of
user resources that contain information about every user in the WMT database
and sends that back formatted as JSON objects.

To search for a subset of users, use */api/users/search*. Some examples are:

1. */api/users/search?username=charliesheen* to get the user whose username
   matches *charliesheen*. Since usernames are unique, only one user will
   be returned.
2. */api/users/search?contains=charlie to get users whose username contains
   the string "charlie".

To create a new user, send a POST to */api/users* along with a JSON body
that gives the requested username and password. There are a couple cases
here that can happen:

1. If the username already exists, that's an error. Return 422 along with an
   error message that says something like the resource already exists.
2. If the username doesn't exist, add an entry to the user database and log
   the user in. The newly-created user is returned as JSON object.

After being logged-in, the user is now able to logout. Simple, just send a
GET to */users/logout*. That's it. Wait, what if they are already logged out?
That's silly. Don't do anything.

Oh no! I forget who I am. I'm pretty sure I'm logged in, but I have so many
WMT accounts I no longer remember who I am logged in as. That's what
*/api/users/whoami* is for. Send a GET, and the server returns the name of
the currently logged-in user.

To do things that are specific to a particular user, send requests to URLs
that start with */api/users/<id>*. <id> is an integer that is specific to
a particular user. If you happen to be the first user in the database, you
send a GET to */api/users/1* to get information about yourself. You can do
other stuff to yourself:

1. Remove your account by sending DELETE.
2. Change your password by sending a PATCH request along with your new
   password.

Clearly, to do these things you'll have to first be logged in.

Once a person begings to use the WMT, they'll have some other stuff associated
with them. For example, users will build models and to help them organize
their work we allow them to tag each model with one or more tags. So,

1. Send a GET to */api/users/1/models* to the all the models that user 1
   has created.
2. Send a GET to */api/users/1/tags to the all the tags that user 1 has
   created.

What about if a person wants a list of all their models that have been
tagged with a particular tag? Oh yeah, we can do that. Try a GET at
*/api/users/1/models/search?tag=winning* to find all of Charlie Sheen's
models that are tagged as "winning". I bet you didn't know Charlie Sheen
was a WMT user.
