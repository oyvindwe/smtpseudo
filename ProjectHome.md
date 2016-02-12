SMTPseudo

SMTPseudo functions as a mail forwarding server.
user can easily write mail flow chain in XML.
Flow rules can be based on the following aspects of messages

1.title contains a rule matching string.
2.contains given string at some position in body part of messages.
3.from address contains a rule matching string

4.can use mail template which will be forwarded in place of the original mail messages received.

5. user can strip strings found in the original messages received and reinject them into prepared mail templates and forward them.
(usage scenario would be "strip username and url links found in the original english mail sent from a service
and re-inject them into a mail template written in another language)