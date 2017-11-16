scala-smtp
=====
Quick and simple API for sending SMTP messages. Inspired by a static web project I've worked on that's hosted on github pages and required a contact form that sends out an email upon completion.

Accepts a POST request containing a `from`, `subject`, `message`, and `ip`. To prevent abuse and spam, the `to` email address is hardcoded in the application config so that if there is spam, it's only going to be sent to the configured email address.
