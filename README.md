# shipmember

A program for sending information to members of groups, associations etc. The program will read a csv file with membership information, parse it and create personal messages for every recipient in the file.

The program has the following features:
* Can send HTML formatted emails (over smtp) with support for template driven personal content
* Can create PDF files with personal content using LaTeX
* Can notify only those who are flagged as having not paid
* Can notify only those who have (or lack) email addresses
* Can notify only a given subset of members
* Can treat several members as part of a single household (see below)

### Households
Several members (i.e. a family, normally) may make up a single household. Information sent out by the program will only be sent to a household once. In other words, a family consisting of several persons will receive information only once. When notifying by email, the first household member that has an email, is the one that will be used.

## Technical
Compile and build using `mvn clean install`

After executing the above command, a jar-file is created in the target directory. Run it using `java -jar shipmember-1.1.0.jar <arguments>`
For information about the arguments, see below.

### Arguments
The following are valid arguments to the program:
* `--input <inputfile>` Mandatory argument. Specifies the csv file with members to read from. An example can be found in src/test/resources/members.csv
* `--output <outputfolder>` Mandatory argument. Specifies where to save PDF files.
* `--email-subject <string>` Optional argument. The subject of emails sent. Enclose multiple words \\\"Like this\\\".
* `--household-numbers <list>` Optional argument. Only consider the given list of household numbers (integers). Other arguments (such as `--only-non-payers`) will apply in addition and may narrow the members down further.
* `--only-non-payers` Optional argument. Makes the program only consider the members who have not paid. Default (if this argument is not given) is that everyone is included.
* `--parse-all` Optional argument. Every household is parsed. Unless narrowed down by other arguments, this is the default.
* `--parse-those-with-emails` Optional argument. Only the households who have an email are parsed.
* `--parse-those-without-emails` Optional argument. Only the households who do not have an email are parsed.
* `--output-pdf-and-send-email` Optional argument. Will create a PDF for every household and send emails to every household that has an email address. Unless overridden by other arguments, this is the default.
* `--output-pdf-only` Optional argument. Will create a PDF for every household, but send no emails.
* `--only-send-email-where-possible` Optional argument. If a household has an email address, it will be sent an email; if not, a PDF is created instead.

### Sending Emails
Emails are sent over SMTP. You will need to fill in the details (host name, port number, whether to use SSL or not, user name and password) of your email provider in order to send emails. This data needs to be put in src/main/resources/application.yml


### Templating
The information to be sent by email is created using the Thymeleaf templating engine. The template is located in src/main/resources/templates/email.html

The PDFs are created using the LaTeX typesetting system. LaTeX is not included in this program, so it needs to be callable from the computer that this program is used on. When run, the program will create a file called personalinfo.tex with information about the current household. It is included in the LaTeX template, which is located in src/main/resources/templates/invite.tex

The LaTeX template is not straight forward to use for the inexperienced since it uses Xetex and fancy custom ornaments. Some help is given in the invite.tex file, but consider replacing the template with your own if you have troubles getting it to work.


## About
Written by Johan Sjöblom.

Written in Kotlin, using Maven and Spring. It has been mutation tested using [pitest](https://pitest.org/).

The name **shipmember** is a play on the word *membership* and the fact that the domain it was written for was *member*s of a sailing association that had several *ship*s.

The LaTeX ornaments in the src/main/resources/pgfornament.zip file belongs to their respective authors and is not covered by the license of this program.
