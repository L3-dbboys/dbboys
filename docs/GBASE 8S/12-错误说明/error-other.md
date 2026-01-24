---
title: 18.Error other
description: 
published: true
date: 2023-02-26T13:00:13.073Z
tags: 
editor: markdown
dateCreated: 2023-02-26T13:00:08.003Z
---

-32100	MAC check failed.

Your session sensitivity label does not permit you to perform the
operation on the OnLine/Secure object that you are accessing because it
violates the MAC policy of GBase OnLine/Secure. Log in at the
appropriate sensitivity label and retry the operation.


-32101	DAC check failed.

Your session identity does not permit you to perform the operation on
the OnLine/Secure object that you are accessing because it violates the
DAC policy of OnLine/Secure. Log in with the appropriate identity, or
obtain the necessary privileges and retry the operation.


-32102	Bad label range.

The range that is specified for an operation that involves labels is
incorrect. The situation could arise either due to bad user input or
an internal error.


-32103	Label comparison operation failed.

This internal error could arise because the labels to be compared are
incomparable or illegal, or the comparison operation was not legal for
the label data type.


-32104	Internal error; no table descriptor.

The table descriptor for the specified table was not found in the core
dictionary.


-32110	Illegal session level for dropping a database.

Your session sensitivity label must equal the sensitivity level of the
database.


-32112	No DBA privilege for creating a view schema.

Contact the database administrator and request DBA privilege.


-32113	No DBA privilege for creating a table schema.

Contact the database administrator and request DBA privilege.


-32114	Cannot drop system catalog tables.

System catalog tables are dropped only when the database is dropped.


-32115	Cannot change ownership of a table.

It is not legal to change the ownership of a table.


-32116	Illegal session level for altering a constraint.

Your session sensitivity level must equal the sensitivity level of the
table.


-32117	Illegal session level for creating an index.

Your session sensitivity level must equal the sensitivity level of the
table.


-32118	No Index privilege for creating an index.

Privilege is not granted for you to create an index on the table.


-32119	Illegal session level for altering an index.

Your session sensitivity level must equal the sensitivity level of the
table.


-32120	No Resource privilege.

The action that you are performing requires Resource privilege on the
database. Contact the database administrator to request the privilege.


-32121	Illegal session level for dropping an index.

Your session sensitivity level must equal the sensitivity level of the
table.


-32122	Cannot modify system catalog tables.

It is not legal to change attributes other than the next-extent size
for the system catalog tables.


-32123	Not the owner of the index.

Only the owner of the index can perform the operation that you are
attempting.


-32124	Cannot modify an index on a temporary table.

It is illegal to alter indexes on temporary tables.


-32125	Current database number out of range.

This internal error indicates that the number of concurrent database
opens exceeds the limit that OnLine/Secure sets.


-32126	Illegal label tag.

The tag value that you supplied is unknown to the operating system.


-32127	Illegal session level for dropping a table.

Your session sensitivity level must equal the sensitivity level of the
table.


-32128	No privilege for changing a SERIAL column.

You need Insert or Alter privilege on the table, or Update privilege on
the SERIAL column, to change the serial value.


-32129	Table was not opened at required label.

This message is applicable only for multilevel secure applications. You
are attempting an operation on a table at a level higher than or
incomparable to the level at which you opened the table. Open the table
at the appropriate level.


-32130	There is no record at the specified level.

This message is applicable only for multilevel secure applications. No
records exist at the level that you are accessing.


-32131	Internal heap error.

This message indicates an internal error.


-32132	Cannot order by label.

Ordering by label is illegal because labels are not ordered linearly.


-32133	Illegal session level for granting table-level privileges.

Your session sensitivity level must equal the sensitivity level of the
table.


-32134	Illegal session level for granting database-level privileges.

Your session sensitivity level must equal the sensitivity level of the
database.


-32135	Illegal session level for revoking table-level privileges.

Your session sensitivity level must equal the sensitivity level of the
table.


-32136	Illegal session level for revoking database-level privileges.

Your session sensitivity level must equal the sensitivity level of the
database.


-32137	No Alter privilege to modify a constraint.

Alter privilege on the table is required to modify a constraint.


-32138	Cannot set the initial SERIAL value.

This message is applicable only for multilevel secure applications. The
initial serial value was not set at table creation.


-32139	No initial value has been set for the SERIAL column.

This message is applicable only for multilevel secure applications. No
initial serial value exists for the sensitivity level that you are
accessing. The initial serial value must be set at table creation if a
SERIAL column exists in the table.


-32140	File handle and tabid are not consistent.

This message indicates an internal error. The table handle that is
provided to an ISAM function is not a legal handle for the table whose
tabid is also provided as a function argument.


-32154	Label.map file must be owned by DBSSO.

Ensure that no other user has created a label.map file in the
$LABELMAPDIR directory. Check that the sensitivity label of the
label.map file is ixdbssoL.


-32155	tbload is locked. Remove label.lok file.

An old label.map file is in use. Update the label.map file if
necessary and remove the label.lok file to indicate your concurrence
that the label.map file is indeed okay.


-32156	Cannot open file.

Cannot open the label.map file or the label.lok file. Check the
existence, permission, and sensitivity level of these files.


-32157	Invalid data in label.map file.

Check the integrity and legality of entries in the label.map file
according to the current operating system.


-32158	The mapped tag does not exist in system.

The translation tag for a tag on tape that is provided in the label.map
file does not exist on the operating system. Check the integrity and
legality of entries in the label.map file according to the current
operating system.


-32159	The mapping tag does not exist in system.

A tag that is supplied in the label.map file does not exist on tape.
Ensure that you are providing mapping only for those tags that are on
the tape.


-32160	Unable to sort label.map file.

An error occurred during sorting of the label.map file. Check the
integrity of the label.map file.


-32162	Label tag are not unique.

Tags and their translation tags should have strictly one-to-one
mapping. Check the label.map file for duplicate tag mappings.


-32163	Label tag are not valid.

Only the tag representation of labels should be stored in the label.map
file. Check the integrity and legality of the tags that are stored in
the label.map file.


-32164	Error creating session shared memory.

This message indicates an internal error. Check the operating-system
error message. If the cause of the error is the size of the shared
memory, you or your DBSA might have set a high value for SM_ROWSIZE or
SM_TOTALROWS in the session configuration file.


-32165	Error attaching to session shared memory.

This message indicates an internal error. Check the operating-system
error message for further information.


-32166	RSAM could not be found or executed by the current user.

Check $GBASEDBTDIR, $SQLEXEC settings. Ensure that you are in the group
ix_users, ix_dbsa, or ix_dbsso. Check that the ISAM executable in
$GBASEDBTDIR/lib is executable by your session.


-32167	Table label inconsistent.

This message indicates an internal error.


-32168	Database label inconsistent.

This message indicates an internal error.


-32169	Cannot convert label between internal and external forms.

This message indicates an internal error. The operating system cannot
map between the internal and external forms of the label. Check the
external or tag representations that you provided.


-32181	The number of estimated security labels must be greater than 0.

Check for the parameter setting in the ONCONFIG file or the input to
ON-Monitor.


-32182	Invalid number of estimated security labels label-name.

Check for the parameter setting in the ONCONFIG file or the input to
DB-Monitor to make sure that the number of estimated security labels is
always greater than 0.


-32183	LUB computation failed.

Check the legality of inputs to the LABELLUB() function.


-32184	GLB computation failed.

Check the legality of the inputs to the LABELGLB() function.


-32190	Cannot aggregate label column.

Check if label column is supplied to an aggregate function.


-32191	Cannot alter table.

Alter table failed; check the additional ISAM error message for further
information.


-32193	Cannot create audit tblspace.

The OnLine/Secure database server cannot be initialized. Note
all circumstances and contact GBase Technical Support.


-32194	Cannot create reserved tblspace.

The OnLine/Secure database server cannot be initialized. Note
all circumstances and contact GBase Technical Support for assistance.


-32197	Not an OnLine/Secure tape.

Use a tape that an OnLine/Secure database server generated.


-32198	Not an OnLine/Secure root chunk.

Modify the ONCONFIG file to refer to a rootdbs that an OnLine/Secure
database server created.


-32400	A Table_option has already been altered.

You can change only one table option (locking mode, extent size) for
each ALTER TABLE session. If you have changed one table option and want
to change another, you must first exit the ALTER TABLE Menu and build
the modified table. Select the Exit option, then the Build-new-table
option. Then select the Table_options option on the ALTER TABLE Menu,
and make your next modification to the table.


-32401	The initial extent size cannot be changed when altering a table.

The initial-extent size is set when the table is first created. The
next-extent size can be altered, but the initial-extent size cannot. To
change the size of the initial extent, you must unload the data from
the table, drop the table, re-create the table with the CREATE TABLE
statement, and reload the data into the table.


-32402	The user cannot change the dbspace name when altering an existing table.

You have specified the location in which to table is to be stored. You
have explicitly specified a dbspace or the dbspace of the database has
already been used. When you attempted to alter a table, you tried to
change the dbspace in which the table is stored. This action is
illegal. You can specify the dbspace only when you create the table.

Download any data in the existing table. Drop that table. Create a new
table. Specify the dbspace that you want to use and upload the data
into the new table.


-32403	Illegal serial length has been used.

This error occurs when the user creates or alters a table and creates
or alters a column of type serial but has specified that the starting
number is less than or equal to zero (illegal) or has entered in a
nonnumeric value. Enter a number equal to or greater than 1 for the
starting number.


-32404	Invalid delimiter. Do not use '\\', hex digits, tab or space.

The delimiter that is specified for the LOAD or UNLOAD statement is
illegal. You cannot use the new-line character, hexadecimal digits (0
to 9, A to F, a to f), the tab character, or a space as a delimiter.
Check the statement and change the delimiter symbol.


-32405	Incorrectly formed hexadecimal value.

The hexadecimal file that is used to load a BYTE value into the table
has an illegal character or an incorrectly formed hexadecimal value.
Check the file for any anomalies and try running your statement
again.


-32406	Value must be greater than zero.

You specified an extent size less than or equal to zero when you
created or altered a table. Specify an extent size greater than zero.


-32407	Trigger not found.

You specified an invalid trigger name. Enter the name of an existing
trigger or correct your spelling.


-32408	Cannot create MODE ANSI database without specifying transaction log
pathname.

You tried to create an ANSI-compliant database on GBase SE but did
not specify a pathname for the mandatory unbuffered transaction
logging. Select the Mode_ansi option of DB-Access again, or revise your
CREATE DATABASE statement and enter the full log pathname.


-32409	Data is unavailable, cannot open database sysmaster.

DB-Access cannot open the sysmaster database, from which you have
requested information. Check that the sysmaster database was built
properly and read the error log for the cause of the failure.


-32410	Syntax not supported by DB-Access.

DB-Access does not support the AS, WITH CURRENT TRANSACTION, or USER
clause of the CONNECT statement, although the database server supports
it. Use proper SQL statement syntax when you run your CONNECT
statement in DB-Access.


-32411	An Alter Table Option has already been altered.

You attempted to perform an ALTER TABLE option on a table option that
you previously altered. You cannot alter a table option more than
once.


-32412	USING clause unsupported. DB-Access will prompt you for a password.

DB-Access does not support the USING password clause in a CONNECT ...
USER statement when it violates security. For example, do not type a
password on the screen where it can be seen or include it in a command
file that someone other than the user can read. To maintain security,
DB-Access prompts you to enter the password on the screen and uses echo
suppression to hide it from view.


-32500	User does not have discrete privilege to change session levels.

You must obtain the PRIV_CANSETLEVEL discrete privilege from the DBSSO
before the start of a session in which you use the SET SESSION LEVEL
statement.


-32501	Login session level not dominating the new session level.

You must log in at a session sensitivity level that dominates the
session level that you specify.


-32502	New session level not dominating the database level.

You cannot access the database at the new session sensitivity level.
Use a different level that dominates the database.


-32503	User tables should be closed to change session attribute.

Close all tables and relinquish all cursors that remain open before
you attempt to change session sensitivity levels.


-32504	Operations on remote objects are not allowed after session level set.

You cannot access objects in remote databases when your current session
sensitivity level differs from that of your login session. Return to
the sensitivity level of your login session to access remote data.


-32505	Cannot set session level.

For more information, refer to the accompanying error message.


-32506	Bad session label format.

The argument to the SET SESSION LEVEL statement was not a valid
sensitivity label.


-32507	Cannot set session authorization.

You must be a DBA to change the session user unless you are changing to
yourself.


-32508	Statement is invalid within a transaction.

You attempted to execute a SET SESSION AUTHORIZATION, SET ROLE, or SET
TRANSACTION statement from within an active transaction.

Issue the SET statement at the beginning of the transaction, before a DML
statement, such as SELECT or INSERT, makes the transaction active. Otherwise,
roll back or commit the transaction before you issue the SET statement.


-32509	Bad session authorization format.

The user name that is supplied as an argument to the SET SESSION
AUTHORIZATION statement is invalid. Supply the user ID of a valid
user.


-32510	User does not have discrete privilege to change session authorization.

You must obtain the PRIV_CANSETIDENTITY discrete privilege from the
DBSSO before the start of a session in which you use the SET SESSION
AUTHORIZATION statement.


-32513	Cannot rename table or column.

Renaming of table or column failed. For more information, refer to the
ISAM error message.


-32514	Session level is different from the level of the database object.

Your session sensitivity label does not permit you to perform the
operation on the OnLine/Secure object that you are accessing because it
violates the MAC policy of OnLine/Secure. Log in at the appropriate
sensitivity label and retry the operation.


-32520	Cannot create SL map tblspace.

This message indicates an internal error.


-32521	Cannot create IL map tblspace.

This message indicates an internal error.


-32522	Cannot create Datalo translation.

This message indicates an internal error.


-32523	Cannot create Datahi translation.

This message indicates an internal error.


-32524	Cannot create ixdataH translation.

This message indicates an internal error.


-32525	Cannot create saved translations.

This message indicates an internal error.


-32526	Saved and stored tags disagree.

This message indicates an internal error.


-32528	Tag not found.

This message indicates an internal error.


-32529	Cannot create ixdbsaL translation.

This message indicates an internal error.


-32532	Illegal data type for aggregation function.

You cannot use the aggregation function on string or DATETIME
data types. Review the use of these functions.


-32766	Unknown error message number.

The software product cannot find the error message text files. Either
the GBASEDBTDIR or DBLANG environment variable is set incorrectly.


-32792	The onutil EBR block command completed successfully.

The database server has been blocked successfully.

You need to unblock the database server before normal processing can
continue.


-32793	The onutil EBR unblock command completed successfully.

The database server has been unblocked successfully.

No action is required.


-33000	The keyword is a reserved ANSI keyword.

This message is a warning. If you want your code to be ANSI compliant,
do not use the keyword that is shown as a variable name. Check the
GBase Guide to SQL: Syntax for alternative syntax.


-33001	Environment variable variable-name has invalid value.

Reset the specified environment variable to a legal value and try
again. See the GBase Guide to SQL: Reference.


-33002	Syntax error in the ESQL INCLUDE statement.

The preprocessor cannot interpret this INCLUDE statement. Refer to your
embedded-language manual for the correct syntax of the INCLUDE
statement.


-33003	Bad label format.

The label on the current statement is incorrect in form. Refer to the
embedded-language manual for accepted label formats.


-33004	Option option-name does not exist or has bad format.

This embedded-language product does not support the specified option
from the preprocessor/compiler command line. Check that it is spelled
as you intended. Refer to the embedded-language manual for supported
options.


-33005	Incomplete string.

A character string is not correctly terminated according to the rules
of the host language. For example, it might be missing an end quote,
not have a continuation character, and so on. The preprocessor might
not recognize the error until a number of source lines beyond the line
where you intended the string to end.


-33006	Type of variable-name is not appropriate for this use.

You cannot use the specified variable in the context of this statement.
Check that you specified the variable you intended and that you
declared it with the proper type. Then refer to the embedded-language
manual for variable usage in this type of statement.


-33007	'$' assumed before variable-name.

The preprocessor has assumed that you intend the symbol variable-name
in this statement as a host variable. (A host variable is normally
indicated with '$' or ':' preceding it.) Check that you did intend this
meaning.


-33008	Record component component-name was not declared.

This statement uses the symbol component-name as if it were the name of
a component of a record, but it was not declared as a component of the
record name with which it is used. Check the spelling of both the
component and the record name.


-33009	The component name component-name has already been used.

The record component was declared twice in the same record/structure.
Check the spelling of component names and the syntax of the
declaration.


-33010	Internal error: Preprocessor states corrupted.

Correct all other processing errors. If the error recurs, note
all circumstances and contact GBase Technical Support.


-33011	Current declaration of variable-name hides previous declaration.

This message is a warning from the preprocessor. The specified variable
was declared more than once in the current scope. Compilation continues
using this latest declaration to the end of the current scope. If you
did not intend to redeclare the indicated variable, check the spelling
of variable names and the syntax of declarations. In GBase ESQL/C,
this warning can also be issued if the variable is used as function
parameter. In this case, use the PARAMETER clause.


-33012	Number of digits must be 1 to 32.

You specified a precision or scale for a DECIMAL value that is out of
range. A DECIMAL variable must have from 1 to 32 digits. Check the
punctuation of the declarations in this statement.


-33013	END DECLARE SECTION with no BEGIN.

The preprocessor has found this EXEC SQL END DECLARE SECTION statement
but has not seen a preceding EXEC SQL BEGIN DECLARE SECTION. Possibly
it was omitted or not recognized due to another error. Check that each
BEGIN DECLARE is paired with an END DECLARE.


-33014	Illegal use of record/structure variable-name.

The indicated record was used where only a simple variable can be
accepted. Review the declaration of variable-name and check that it
is the variable you intended to use (possibly you need to qualify it
with a component name). Refer to the embedded-language manual for the
requirements of this statement.


-33015	Input file name has invalid suffix suffix-text.

The name of the input file that is submitted to the preprocessor must
have the correct suffix. Check the command syntax and rename the file
if necessary. The following file suffixes are required:

    *   .ec for GBase ESQL/C

    *   .eco for GBase ESQL/COBOL

    *   .ef for GBase ESQL/FORTRAN


-33016	Macro definition for macro-name is incomplete.

This preprocessor macro definition statement does not have the ESQL
statement terminator. Add a semicolon(;) to the end of the statement.


-33017	Incomplete statement.

The preprocessor cannot recognize the end of the current statement.
Check the syntax of this and preceding lines. Look for omitted ending
semicolons, omitted quote marks, or omitted end-of-comment symbols.


-33018	Indicator variables are not allowed in this clause.

This statement includes a specification of a host variable with an
indicator variable, but no indicator variable is allowed in this
context. Review each use of an indicator variable and remove the
unnecessary ones.


-33019	Label is too long.

This statement specifies a label that is longer than this embedded
language allows. Check the spelling and punctuation of the statement.
Refer to the embedded-language manual for proper formation of label
names.


-33020	Line is too long.

This line is too long either for the host language or to fit into the
internal buffers of the preprocessor. Refer to the embedded-language
manual and to the host-language manual for rules on continuing long
lines.


-33021	The value of macro macro-name is too long.

The specified value for the macro is too long to fit in the internal
buffer. Shorten the statement and try again.


-33022	Include path name too long.

The pathname in this INCLUDE statement is too long to fit in the
internal buffers. Check the punctuation of the statement; possibly an
end-quote has been omitted. If not, you will have to find a way to
specify the included file with a shorter pathname. Refer to the
embedded-language manual for the rules of the preprocessor on searching
for included files.


-33023	Quoted string too long for SQL.

The quoted string is too long to fit in the internal buffers. Check the
statement for a missing end quote.


-33024	Macro name expected.

The macro name of the macro statement is missing. Specify the name and
try again.


-33025	The name '<identifier>' is too long.

The specified SQL identifier is too long. The maximum length for identifiers
depends on the database server. In GBase Database Server 9.2 or later, the
maximum length is 128 characters. In other GBase database servers, the
maximum length is 18 characters.

Check the punctuation of the statement and the spelling of identifier. If
the spelling and punctuation are as you intended, you need to change the
declaration to use a shorter identifier.


-33026	Blocks cannot be nested more than n levels.

The preprocessor limits the number of nested levels to the specified
value. Review the program structure preceding this point; the
punctuation or keywords that close a block might have been omitted or
might not have been recognized due to an earlier error. If all is as
you intended it, you will have to reorganize the code to use fewer
nested blocks.


-33027	Record nesting too deep. Maximum is n.

The preprocessor limits the number of levels to which records (data
structures) can be nested. Work backward from this point and review
the declaration of records. Possibly the punctuation or keywords that
close a record have been omitted or have gone unrecognized due to an
earlier error. If all is as you intended it, you will have to simplify
the data structure.


-33028	Invalid compiler-name compiler type compiler-type-name.

The compiler type must be specified as a command-line option to this
preprocessor. Refer to the embedded-language manual.


-33029	No input file given.

No input file is named on the command line.


-33030	Cannot have a insert statement on a SCROLL cursor.

This DECLARE statement specifies the SCROLL keyword, but it goes on to
specify an INSERT statement. Insert cursors cannot use the SCROLL
keyword.


-33031	Statement label is not allowed in this statement.

This message is a warning only. You used a label on a statement that
does not generate any real code in the output file. Refer to the
embedded-language manual for the correct use of labels.


-33032	WITH NO LOG can only be specified for TEMP tables.

You used the WITH NO LOG option in the wrong context. Use it only when
you create a TEMP table and you wish to exclude transaction log
operations on it. See the GBase Guide to SQL: Syntax for the SQL
syntax and proper usage of the WITH NO LOG option.


-33033	The field field-name is an GBase extension to XPG3 X/Open standard.

This message is a warning only. You used the -xopen option, asking that
the source file be checked for compliance with the XPG3 X/Open
standard. The ITYPE, IDATA and ILENGTH field names are GBase
extensions to the standard.


-33034	POWER cursors not available.

POWER cursors are not supported. Refer to your embedded-language
manual.


-33035	A qualifier has not been specified.

You attempted to use a DATETIME qualifier, but you did not specify a
specific range of acceptable values for that qualifier. Define the
ranges that you will use to qualify the DATETIME qualifier. The
DATETIME qualifier must have a beginning and ending range (for
instance, year to month, day to hour, and so on).


-33036	This line does not conform to ANSI X3.135-1989.

This message is a warning only. You used the -ansi option, asking that
the source file be checked for compliance with the current ANSI
standard. This statement uses an GBase extension to the ANSI
standard.


-33037	Name is not a component of record record-name.

Name is used as if it were a member of the record record-name, but no
member of that name exists. Check the spelling of the two names, and
review the declaration of record-name.


-33038	This statement does not conform to the X/Open standard.

This message is a warning only. You used the -xopen option, asking that
the source file be checked for compliance with the XPG4 X/Open
standard.


-33039	Updates are not allowed in singleton select.

You have an UPDATE statement in combination with a SELECT statement
that returns only one row. The UPDATE statement requires a cursor that
has been declared FOR UPDATE. See the DECLARE, SELECT, and UPDATE
statements in the GBase Guide to SQL: Syntax for more information
about cursors.


-33040	Object hostvar was not declared.

The host variable hostvar is either not declared, or it is misspelled.
If hostvar is not declared as a host variable, declare it. Otherwise,
correct the misspelling.


-33041	Cannot open output file outfile.

The preprocessor cannot open outfile because not enough disk space is
available, or you do not have file-system permission to open the file.
If insufficient disk space is available, delete files or select another
disk to make space available in the file system. If you do not have
file-system permission to open outfile, specify another directory or
login with the required permission.


-33042	Cannot open input file inputfile.

The embedded-language preprocessor cannot locate the file that you are
trying to include in your program with the -I option. Check that the
file is in the location that you have specified and that you have
specified the location correctly. Also check that you have permission
to open inputfile; if you do not, specify another directory or login
with the required permission.


-33043	Out of memory.

The preprocessor was unable to allocate more memory. If possible,
reduce the number of processes that are running at the same time as the
preprocessor, or reduce the size of the program. Check that adequate
swap-disk space exists. On DOS systems, you will need to free some
disk space.


-33044	Precision must be greater than 0 and less than the specified number
of digits.

You specified the precision and/or the scale for a DECIMAL value
incorrectly. See the GBase Guide to SQL: Reference and
the appropriate GBase ESQL manual for proper usage.


-33045	Overriding the previous definition of macro macro-name.

This message is a warning only. You have defined the macro macro-name
more than once. The preprocessor uses the latest occurrence of
macro-name.


-33046	Indicator cannot be used with records.

This message is a warning only. You cannot specify an Indicator
variable for input to a host record or data structure. The indicator is
ignored. An indicator variable must be associated with a single data
item. If you need to use an indicator with a particular member of this
record, you must list all the record members.


-33047	Record record-name is not allowed in this clause.

In this statement, you use the specified record where only a simple
variable is allowed. Check the spelling of names and rewrite the
statement using a simple variable. See the embedded-language manual and
the GBase Guide to SQL: Syntax.


-33049	Field type field-name has been used out of context.

The specified field-name is used incorrectly in the GET/SET DESCRIPTOR
statement. See the GET DESCRIPTOR and SET DESCRIPTOR statements in the
GBase Guide to SQL: Syntax for the correct syntax.


-33050	The type or subtype type-name has already been used.

The statement redefines a user-defined type or subtype that has already
been defined. This action is not allowed. Review the declarations, and
use a unique name for this type.


-33051	Syntax error on identifier or symbol symbol-name.

An error in syntax was found at or near symbol-name. Check the GBase
Guide to SQL: Syntax for the proper use of identifiers and the
appropriate embedded-language manual for the proper use of other
symbols.


-33052	Unmatched ELSE.

A matching IFDEF or IFNDEF statement does not precede this ELSE
statement. Review the source lines that precede this point; the
preceding statement was omitted or might not have been recognized due
to an earlier error.


-33053	Unmatched ENDIF.

An IFDEF or IFNDEF statement does not precede this ENDIF statement.
Review the source lines that precede this point; the preceding
statement was omitted or might not have been recognized due to an
earlier error.


-33054	Updates are not allowed on a scroll cursor.

This UPDATE statement refers to a cursor that was declared with the
SCROLL keyword. The UPDATE statement requires a cursor that has been
declared FOR UPDATE to ensure that the proper level of locking is
applied to the rows that will be updated. See the DECLARE statement in
the GBase Guide to SQL: Syntax for more information on the correct
use of cursors.


-33055	The name id-name has already been used.

The SQL identifier id-name has already been declared. Check the
spelling of names and the declarations in the current name scope.


-33056	Error errno during output.

The error errno occurred during output. Check that sufficient disk
space is available for the output file and that you have the necessary
file-system permissions for the file location. Look up errno in the
host operating-system manual (or see the list that begins on page 3)
for the specific cause of the problem and the appropriate corrective
action.


-33057	Cannot open error log file errorfile.

You specified the log preprocessor option to have error and warning
messages sent to errorfile rather than to standard output. However, the
preprocessor cannot open errorfile because not enough disk space is
available, or you do not have file-system permission to open the file.
If insufficient disk space is available, delete files to make space
available in the file system, or select another disk. If you do not
have file-system permission to open errorfile, specify another
directory or login with the required permission.


-33058	Option-name is not a valid default option for the column definition.

See the GBase Guide to SQL: Syntax for the valid DEFAULT clause
options in a column definition.


-33060	Invalid expression.

The specified expression is incorrect. Refer to the GBase Guide to
SQL: Syntax for the correct syntax and usage for this expression.


-33061	Unable to expand recursive macro macro-name.

The preprocessor cannot fully expand the macro-name macro because a
loop exists in the defined macros. Correct the macro and try again.


-33062	Missing ENDIF.

An IFDEF or IFNDEF statement has no matching ENDIF statement prior to
the end of the source file. Work upward from the end of the file, and
check that each IFDEF and IFNDEF statement is paired with a matching
ENDIF. Possibly the ENDIF was not recognized due to another error.


-33063	Already within BEGIN DECLARE SECTION.

This message is a warning only. An EXEC SQL BEGIN DECLARE SECTION
statement is inside another BEGIN/END DECLARE block. This statement is
ignored. However, the warning might indicate a mix-up in the structure
of your program. Check that all the declaration sections are properly
delimited.


-33064	variable-name has appeared before with a different case.
        ESQL/language-name is case-insensitive.

This message is a warning only. The specified variable appears more
than once with different combinations of uppercase and lowercase
letters. Because this product is not case sensitive, all these names
are treated as one. If you intended these names to indicate different
variables, change the name (not merely the case) of one of the variable
declarations and recompile.


-33065	Cursor/statement id '<name>' is too long when prefixed by
module name.

This message is only a warning. When you use the -local preprocessing option,
cursor names and statement names are prefixed with a unique tag generated
from the module name. (On UNIX, the inode number of the source program is
used as the unique tag.) The combined length of the cursor or statement name
and the unique tag should not exceed the maximum length for identifiers,
which depends on the database server. In GBase Database Server 9.2 or later,
the maximum length is 128 characters. In other GBase database servers,
the maximum length is 18 characters.

In the case of <name>, the combined length of the name and unique tag exceeds
the maximum length for identifiers. As a result, if the same name is used in
a different source module, the two names might not be distinct, as the -local
option requests.


-33066	Cursor/statement ids <id-1> and <id-2> are not unique in first
	128 characters when prefixed by module name.

When you use the -local preprocessing option, cursor and statement names are
prefixed with a unique tag that is generated from the module name. (On UNIX,
the inode number of the source program is used as the unique tag.) In this
case, <id-1> and <id-2> are at least 124 characters long, and their first 123
characters do not differ. As a result, they are the same when the tag is
added and the result is trimmed to 128 characters.

Change the two names to avoid name collision and try again.

This message applies to GBase Database Server 9.2 or later.


-33066	Cursor/statement ids <id-1> and <id-2> are not unique in first 18
characters when prefixed by module name.

When you use the -local preprocessing option, cursor and statement names are
prefixed with a unique tag that is generated from the module name. (On UNIX,
the inode number of the source program is used as the unique tag.) In this
case, <id-1> and <id-2> are at least 14 characters long, and their first 13
characters do not differ. As a result, they are the same when the tag is
added and the result is trimmed to 18 characters.

Change the two names to avoid name collision and try again.

This message applies to GBase Extended Parallel Server, GBase
Server with Advanced Decision Support and Extended Parallel Options,
GBase OnLine XPS, GBase Database Server, GBase Database Server,
GBase Database Server, and GBase SE.


-33067	ELIF without IFDEF.

An IFDEF or IFNDEF statement does not precede this ELIF statement.
Review the source lines that precede this point. The preceding
statement was omitted, or it might not have been recognized due to an
earlier error.


-33068	ELIF after ELSE.

The ESQL preprocessor ELSE statement indicates the last part of an
IFDEF or IFNDEF statement; another ELIF part might not follow. Review
the contents of this IFDEF or IFNDEF statement and put its parts in
order.


-33070	Stack overflow occurred during statement parse.

This message, which indicates that the parser stack has overflowed,
rarely occurs. It might occur, for example, if your embedded-language
statement (SELECT, INSERT, UPDATE, DELETE, and so on) contains an
extremely large or complicated clause (WHERE, MATCHES, LIKE, and so on)
that the parser is trying to process. In that case, break up the
clause and try again.


-33071	The statement id statement_ID has already been used.

This warning message indicates that you have used two PREPARE statement
identifiers with the same name. For example:

exec sql prepare stmt_id from "select * from tab";

exec sql prepare stmt_id from "insert into tab values(1)";


-33074	Explicit cast, either with CAST keyword or (::) operator, is not
allowed in client collections.

You cannot use explicit casts in client collections. For example, the
following example is not allowed:

   $client collection list(row( a udt_1, b list(int))) mylist;
   $insert into table (:mylist) values (CAST(1 AS udt_1), list{1,2,3});
   $insert into table (:mylist) values (1::udt_1, list{1,2,3});

When you enter esql -e client.ec, you get the following errors:

esqlc: "client.ec", line 2: Error -33074: Cannot use CAST in client
collections.

esqlc: "client.ec", line 3: Error -33074: Cannot use CAST in client
collections.


-33075	Cannot use user-defined routines in client collection/row constructors.

The user has tried to call a user-defined routine function in a ROW or
COLLECTION constructor for a client collection or row host variable.
Check the syntax of the statement and change the statement so that it
does not call a user-defined routine. User-defined routines can be
executed only on the server.


-33083	Cannot open file file_name because of too many open files.

The preprocessor cannot open the file because too many files
are open. Check your system resources to see if you can increase the number
of file handles (descriptors). Close unnecessary open files. Also check for
mistakes that might cause recursive file inclusion.


-33084	The lvarchar host variable('<variable-name>') should be an array
or a pointer.

This message is only a warning; compilation continues. ESQL/C does not know
the size of the lvarchar host variable. Although syntactically correct,
this might cause a problem in your application.

Specify the variable as an array or a pointer.


-33085	The username or the password was not provided in a TRUSTED connection.
Both are required.

Any CONNECT statement that includes the TRUSTED keyword must also include 
the USER Authentication clause. To establish a trusted connection, this clause 
must include a valid username and a valid password for that username, 
as in this example:  

EXEC SQL CONNECT TO "testdb1" AS "s1" USER 'zelaine' USING :var_zpass 
   WITH CONCURRENT TRANSACTION TRUSTED;

The USER Authentication Clause requires these syntax elements:

    *   A quoted string (or a host variable) specifying a valid authorization 
        identifier must immediately follow the USER keyword. 

    *   A host variable that evaluates to the password for that authorization 
        identifier must immediately follow the USING keyword.

CONNECT statements that do not include the USER Authentication clause cannot 
include the TRUSTED keyword.


-33200	Invalid statement on symbol variable-name.

The specified type was not defined or a $ character was misplaced in a
statement. Check for misspellings, misplaced $ characters, or undefined
types.


-33201	Fixed character pointers are not allowed.

Fixchar character pointers are not allowed in this context. Replace the
fixchar pointer with a character pointer.


-33202	Incorrect dimension on array variable variable-name.

You referenced the array variable with an incorrect dimension. Correct
the dimension and retry.


-33203	Incorrect levels of indirection on variable variable-name.

A pointer variable is used with the wrong number of level indirection.
Check the indirection levels in this statement.


-33204	Right curly brace found with no matching left curly brace.

The code either includes a stray closing brace (}) or is missing an
opening brace ({). Check the code for unmatched curly braces or other
incorrect punctuation such as a missing end-quote or end-comment.


-33205	PARAMETER cannot be used inside of a C block.

The PARAMETER statement is allowed only in a function declaration
block, not within a namely block that is nested in a function.


-33206	Qualifiers for variable-name not initialized.

This message is a warning only. Due to the complexity of the specified
DATETIME or INTERVAL variable, the qualifiers of its elements cannot be
initialized properly. Compilation continues, but the variable might not
be initialized.


-33207	Type typedef-name too complex for ESQL/C.

The definition of the typedef variable is too complex. ESQL/C does not
support the use of multidimensional arrays or unions in a typdef.
Simplify the typedef.


-33208	Runtime error is possible because size of host-variable-name is unknown.

This message is a warning only; compilation continues. ESQL/C does not
know the size of the host character variable. If the variable is used
in an INTO clause, memory might be overwritten. Specify the variable as
a character array with a numeric size. The numeric size can be a
literal value or an ESQL/C macro value.


-33209	Statement must terminate with ';'.

This message is a warning. This statement does not have the necessary
semicolon. Even though the preprocessor can proceed without any
problems, you should add a semicolon to the statement. Then you can
avoid future problems if code is added to this program.


-33500	filename: Bad environment variable on line number.

The entry on the specified line in the specified environment
configuration file is incorrect. Modify your entry in the file (the
environment variable name and/or setting) and try again.


-33501	Mapping file for DBAPICODE is not found.

The SQL API cannot find the character-mapping file for the specified
DBAPICODE environment variable setting and the standard code set. Check
that the mapped code set exists in the message directory for your
platform. In NLS-ready systems, the standard code set is defined in
the LANG environment variable. In systems that are not NLS ready, the
standard code set is the default 8-bit character set.


-33502	Mapping file does not have the correct format.

The mapping file for the specified DBAPICODE environment variable is
formatted incorrectly. The text file for the character mapping table
can consist of any number of lines. A line can be a comment, or a
one-to-one character map of a DBAPICODE character code to the
equivalent character in the target code set. The text file should be
written in U.S. ASCII or the code set that has the equivalent
representation of U.S. ASCII for the significant characters (#,
parentheses, numeric characters).


-33988	External Space creation failed.

The server could not add the external space. Check the error returned with
this failure.


-33990	External Space drop fails. 

An attempt to drop the external space failed. Check the error associated with
this failure.


-34380	Input stream contains an illegal multi-byte character.

The ESQL/C source has illegal multibyte characters. Review the source
file.


-34381	Input stream ends in the middle of a valid character.

The ESQL/C source file ends in the middle of a valid multibyte
character. The source file might be truncated. Review the source file.


-34382	A system error occurred while reading the input stream.

A system error num occurred while the ESQL/C preprocessor read the
ESQL/C source file. Look for operating-system messages to determine the
cause of the problem.


-34383	An unknown error num occurred while reading the input stream.

An unknown error num occurred while the ESQL/C preprocessor read the
ESQL/C source file. Check that the source file is a valid ESQL/C source
file.


-34388	Invalid character has been found. Cannot continue the processing.

An internal function, while parsing the elements in an SPL routine,
has encountered an illegal character during multibyte processing. The
following options are passed:

    *   database@dbserver;owner.procname

    *   database;owner.procname

    *   database;procname

    *   database@dbserver;procname

    *   owner.procname

    *   procname

Review these options for possible illegal characters.


-34389	Illegal character has been found in the input string.

The SQL script has illegal characters. Review the script file.


-34390	Invalid delimiter; Don't use '\\', SPACE, HEX or Multibyte chars.

The delimiter specified for the FILE statement is illegal. You cannot
use a new- line character, backslash, space, tab, hexadecimal digit (0
to 9, A to F, a to f), or multibyte character as a delimiter. Check the
statement and change the delimiter symbol.


-34393	GLS codeset conversion initialization failed.

The code-set conversion initialization failed during the session
initialization. Not enough memory is available for the code-set
conversion table.


-34394	Session Initialization failed on bad locale name: locale-name

The session initialization failed. An invalid locale name has been
supplied for the locale initialization in the session initialization.
The environment variable to specify the locale name has an incorrect
value. Check the value of the corresponding environment variable,
CLIENT_LOCALE or DB_LOCALE.


-34395	Illegal multibyte character. Line# num

An illegal multibyte character has been detected on the line of the
loadable menu source file. Review the source file.


-34396	Illegal wide character. Line# num

This internal error indicates that the conversion of internal
wide-character formats to multibyte characters failed. Check the
program logic.


-35580	General Table Manager: No table allocated.

The table has not been created or was deleted.


-35583	General Table Manager: Duplicate keys not allowed.

Table entries with the same key are not allowed.


-35584	General Table Manager: Table is full.

Table is full and cannot be extended.


-35585	General Table Manager: Entry is locked.

Entry of a table is already locked.


-35586	General Table Manager: Table is locked.

Table is already locked.


-35587	General Table Manager: No entry.

The required entry does not exist.


-38000	Unknown throwable: (java.security.AccessControlException: access
denied (java.security.SecurityPermission getPolicy)).

This error might occur if there is no gbasedbt.policy file in
$GBASEDBTDIR/extend/krakatoa.

If you do not have particular security concerns, you can copy the
file gbasedbt.policy.std to gbasedbt.policy.


-38514	Set transport buffers size and count FAILED.

The transport buffer size has changed since the last backup.
Return code 120 displays.

Set the transport buffer size in the ONCONFIG file back to its value at
the time of the backup and try the restore again. You might have to refer
to file-system backups of the ONCONFIG file to get the previous value.


-38515	Free transport buffer FAILED.

An error occurred when the database server tried to free the transport
buffers.

Note all circumstances, save a copy of the ON-Bar activity log and
database server message log, and contact GBase Technical Support.


-38516	Client process not registered.

The onbar-worker process is not registered with the GBase server.

Recommended corrective actions follow:

   1. Use the onstat -g bus command to make sure that the onbar-worker
      process is running. Start a new onbar-worker process, if needed.

   2. If an onbar-worker process is running, try ending it with the
      following command and starting a new onbar-worker process:

         kill -2 <worker_process_id>

If this problem persists, contact GBase Technical Support.


-38573	Error: pointer is NULL (<file> <line>).

Internal error. A pointer variable unexpectedly is NULL.

Contact GBase Technical Support for assistance.


-38574	Creating pipes failed. errno = <error number>.

Cannot create output pipes. The 'errno' is the number of the operating system 
error returned.

Refer to your operating system documentation.


-38575	Creating a process failed. errno = <error number>.

The system call to create or fork a process failed. The 'errno' is the
number of the operating system error returned.

Refer to your operating system documentation.


-38576	Closing the unused end of a pipe failed.

The system call to close the unused end of the filter pipe failed.

This error is internal. Retry the operation and if it
continues to fail, contact GBase Technical Support.


-38577	Duplicating the file descriptors failed. errno=<error number>.

The system call to duplicate file descriptors failed. The 'errno' is the
number of the operating system error returned.

Refer to your operating system documentation.


-38578	Executing the backup and restore filter <filter program> failed. errno = <error number>.

The system call to execute the backup and restore filter program failed.
The 'errno' is the number of the operating system error returned.

Refer to your operating system documentation.

-38579	Writing to the backup and restore filter failed. errno = <error number>.

The system call to write to the backup and restore filter pipe failed. The 'errno' is the
number of the operating system error returned.

Refer to your operating system documentation.


-38580	Reading from the backup and restore filter failed. errno = <error number>.

The system call to read from the backup and restore filter pipe failed. The 'errno' is the
number of the operating system error returned.

Refer to your operating system documentation.


-38581	The peek system call on input from the backup and restore filter failed. errno = <error number>.

The peek system call on input from the backup and restore filter pipe failed.
The 'errno' is the number of the operating system error returned.

Refer to your operating system documentation.


-38584	Thread creation for reading from the backup and restore filter failed.

Creating a thread that reads input from the backup and restore filter program has failed.

Refer to your operating system documentation.


-38585	Create event failed.

Creating an event failed.

Refer to your operating system documentation.


-41000	Error in reading the [INET_CONNECTION] section of the GBASEDBT.INI file.

Check the [INET_CONNECTION] section of the GBASEDBT.INI file to make
sure this section has the following section heading:

[INET_CONNECTION]


-41001	The GBASEDBT.INI file does not have a hostname or has a format error.

Check the GBASEDBT.INI file. If you did not define a host name variable
in the InetLogin structure, the [INET_CONNECTION] section must have a
host name. The host name must be defined in the [INET_CONNECTION]
section with the following format:

host=hostname


-41002	The GBASEDBT.INI file does not have a username or has a format error.

Check the GBASEDBT.INI file. If you did not define a user name variable
in the InetLogin structure, the [INET_CONNECTION] section must have a
user name. You must define the user name in the [INET_CONNECTION]
section with the following format:

user=username


-41004	The GBASEDBT.INI file does not have a protocolname or has a format
error.

Check the GBASEDBT.INI file. If you did not define a protocol name
variable in the InetLogin structure, the [INET_CONNECTION] section must
have a protocol name. You must define the protocol name in the
[INET_CONNECTION] section with the following format:

protocol=protocolname


-41005	Error in reading the [INET_PROTOCOL] section of the GBASEDBT.INI file.

Check the [INET_PROTOCOL] section of the GBASEDBT.INI file to make sure
this section has the following section heading:

[INET_PROTOCOL]


-41006	Protocolname is not in the [INET_PROTOCOL] section of the GBASEDBT.INI
file.

Check the GBASEDBT.INI file. The protocol name that is specified in the
[INET_CONNECTION] section should match the protocol name that is
specified in the [INET_PROTOCOL] section. The protocol name must define
the correct .DLL module for that protocol. If you are using an IPX/SPX
protocol, then ipx=INETIPX.DLL. If you are using a Windows Sockets
1.1-compliant TCP/IP protocol, then tcpip=INETWSOK.DLL.


-41007	Error in loading GBase NET for Windows library.

Check your path. If you are using a Windows Sockets 1.1-compliant
TCP/IP protocol, the INETWSOK.DLL module must be in your path. If you
are using the IPX/SPX protocol, the INETIPX.DLL module must be in your
path. These modules must be in your path. In addition, you might not
have enough memory to load the correct DLL module.


-41008	Number of protocols exceeds the number of protocols that are supported
in this version.

You must use only one protocol at a time to communicate with your
remote database server.


-41009	Error in closing connection.

An error occurred when the connection was closed. No corrective action
is provided.


-41020	Connection error.

Too many applications are active under Windows. Close some of your
applications and try again.


-41021	Connection Busy.

You are currently making a database server request. You cannot make
another argument until the current request is finished.


-42306	Could not set lock mode to wait.

The utility that you were running could not access the contents of a
table because it could not wait for a table lock. Wait a while and
retry running dbschema or dbexport.


-43000	ON-Bar backup usage.

The backup command was entered incorrectly. For details, see the Backup
and Restore Guide.

Revise the command and try again.


-43001	ON-Bar restore usage.

The restore command was entered incorrectly. For details, see the Backup
and Restore Guide.

Revise the command and try again.


-43002	ON-Bar session usage.

The session command was entered incorrectly. For details, see the Backup
and Restore Guide.

Revise the command and try again.


-43003	onbar_w usage.

The onbar_w command was entered incorrectly. For details, see the Backup
and Restore Guide.

Revise the command and try again.


-43004	The connection-address is determined by the XCC package.

A command was entered incorrectly. This message might indicate an
error in ON-Bar, as this process should be started only by an onbar
command-line process and rarely by hand.

To start onbar_m by hand, use the ON-Bar activity log of the
command-line process (BAR_ACT_LOG or /tmp/bar_act.log) to determine
the correct value and try again. If this message was issued
automatically, contact GBase Technical Support.


-43005	The syntax <syntax-1> will not be supported in future
releases - use <syntax-2>.

The command-line syntax for ON-Bar backup has changed. The current action
will complete as requested.

In the future, use the new ON-Bar syntax. To use this new syntax, you might
have to change scripts you have written.


-43006	onsmsync usage.

The onsmsync command was entered incorrectly. For details, see
the Backup and Restore Guide.

Revise the command and try again.


-43010	Bad option usage: -f option requires a filename.

The user entered the -f option and did not specify a filename. ON-Bar does
not know which storage spaces to use in this command, so it cannot continue.

Specify the name of a file that contains the list of storage spaces ON-Bar
should act upon, after the -f option, and rerun the command.


-43011	The -f command is ignored for whole system backup/restore and
fake backup.

The user entered the command options -w -f <filename> or -F -f <filename>.
Because whole-system backup, whole-system restore, and fake backup affect all
spaces, the backup and restore will continue and the -f <filename> option
will be ignored.


-43012	Setting backup level to 0 for this command.

Only level-0 backup is supported for a fake backup or external backup.
For GBase Database Server 9.2 or later, this message also applies to backup
verification. The backup level is reset to 0.

No action is required.


-43013	Unable to read backup level, defaulting to level <level-number>.

The backup level entered on the command line is not valid. A level-0 backup
is automatically performed instead.


-43014	Unable to read logical log ID.

The logical log ID entered on the command line is not valid.

Verify that the logical log ID is correct and retry the command.


-43015	Restore cannot be restarted. No unfinished restartable restore
found.

The restore cannot be restarted because no restore file exists in the
$GBASEDBTDIR/etc or %GBASEDBTDIR%\etc directory. This means that no previous
restore exists or that the restore succeeded.

Use 'onbar -r' for a first-time restore.


-43016	Invalid Point In Time value specified: <value>.

Contact GBase Technical Support.


-43017	A Point in Log restore is permitted only during a Full restore.

You cannot do a point-in-log restore when the database server is online.

Do a cold restore.


-43018	A point-in-time restore is permitted only during a full restore.

You cannot do a point-in-time restore unless all spaces are restored.

Do not specify any spaces on the command line.


-43019	Invalid serial number. Consult your installation
instructions.

An error occurred during the installation of ON-Bar.

Ask your database server administrator to reinstall ON-Bar.


-43020	Storage space names ignored for fake backup, whole system
backup/restore, log restore, or log salvage.

A fake backup or whole-system restore backs up and restores all storage
spaces. Storage spaces are not included in the log restore or log salvage.


-43021	Expiration specification <spec> is invalid for the <option-name>
option.

The value given to the named command-line option is inappropriate for
that option. Either the format is incorrect or the option is missing.

Compare the value of the option to the usage statement, revise the command,
and try again.


-43022	Cannot expire all versions of all objects. Must give -g option
a value > 0.

The onsmsync command will not remove all backup objects, which is the meaning
of -g 0.

Try onsmsync again with a positive integer argument to -g.


-43023	Bad option usage: -n option requires a log number.

The user entered the -n option and did not specify a log number. ON-Bar does
not know which log to restore to in this command, so it cannot continue.

Specify the log number, after the -n option, and rerun the command.


-43024	Chunk rename failed.

An error occurred while mapping old chunk location to new chunk location.

Check the rename options of your command line and the mapping, then retry.
If the error persists, refer to the database server message log for more
information. If the cause of the error is still unclear, contact
GBase Technical Support.


-43025	Chunk rename failed.

An error occurred while mapping old chunk location to new chunk location.

Refer to the database server message log for more information.
Check the rename options of your command line and retry the command.
If the error persists and the cause of the error is still unclear,
contact GBase Technical Support.


-43035	You must be user root or gbasedbt to run ON-Bar.

Only users root and gbasedbt or members of the bargroup group are
allowed to execute ON-Bar.

Log in as user root or gbasedbt, or have your system administrator
add your login name to the bargroup group before you attempt the backup
or restore.


-43036	You must be a member of gbasedbt-admin group to run ON-Bar.

Only users listed in the gbasedbt-admin group are allowed to execute ON-Bar.

Ask your Database System Administrator to add your username to the
gbasedbt-admin Group.


-43037	You must be user gbasedbt to run ON-Bar.

Only users gbasedbt are allowed to execute ON-Bar.

Log in as user gbasedbt before attempting the backup or restore.


-43038	The ON-Bar log file cannot be a symbolic link.

The ON-Bar log file that ON-Bar is attempting to open is a
symbolic link.  For security reasons, this is not permissible.

Remove the symbolic link or change the onconfig file so that the
ON-Bar parameters BAR_DEBUG_LOG and/or BAR_ACT_LOG in your ONCONFIG file
point to non-symbolic linked files.


-43039	The ON-Bar log file must be owned by user gbasedbt.

The log file that ON-Bar is attempting to open is owned by a user
other than user gbasedbt.

Change the ownership of the log file to user gbasedbt, or change
the BAR_ACT_LOG and/or BAR_DEBUG_LOG values in the ONCONFIG
file to point to different log files.


-43040	Supplied point in time later than latest log - using latest
logfile <unique ID>.

The point in time specified on the command line is later than the last
logged transaction. The time of the latest logged transaction will be used
instead.


-43041	There were partial logfiles - timestamp 0x<timestamp1> pit
0x<pittime2>.

This is an ON-Bar progress message.


-43042	All logfiles complete - timestamp 0x<timestamp1> pit 0x<pittime2>.

This is an ON-Bar progress message.


-43043	All logfiles complete - timestamp 0x<timestamp1> rolling forward.

This is an ON-Bar progress message.


-43044	Consistent global timestamp not found - recovery is not
possible.

ON-Bar was unable to find the correct log to restore in the emergency
boot file.

Verify that all log files have been either backed up or salvaged and
that each has an entry in the emergency boot file. If everything appears
to be correct, contact GBase Technical Support.


-43045	There are no storage space backups before the specified
point in time - recovery is not possible.

The specified point-in-time value is earlier than the earliest backup
entry in the emergency boot file.

Verify that the point-in-time value entered on the command line is
correct. If it is, the emergency boot file might have been corrupted
or is missing and needs to be restored.


-43046	boot_find_first_logs failed <value-1>.

ON-Bar failed to find the correct log files to restore in the
emergency boot file. This could happen if your system has run out of
memory or if the emergency boot file is missing or corrupted.

Verify that there is sufficient memory in your system to complete a
restore. If there is, the emergency boot file might need to be restored.


-43047	Send bootfile....

This is an ON-Bar progress message.


-43048	Error received from node <node-id> "<error-text>".

On-Bar attempted to communicate with <node-id> and failed.

Verify that all coservers on all nodes are running properly. Try the
restore again. If it still fails, contact GBase Technical Support.


-43049	Error writing to the ON-Bar emergency boot file. Before
attempting a restore, insert the following into <filename>:
<text>.

An error occurred when ON-Bar attempted to write information to the
ON-Bar emergency boot file. The backup of this object succeeded;
however, the object will not be included in a cold restore unless the
specified information is added to the emergency boot file.

To include the object in a cold restore, ask your database server
administrator to insert the specified data into the ON-Bar emergency
boot file or try running onsmsync -b. (The emergency boot file is
$GBASEDBTDIR/etc/Bixbar_<hostname>.<dbservernum> for GBase Extended
Parallel Server, $GBASEDBTDIR/etc/ixbar.<dbservernum> for GBase
Dynamic Server 9.2 or later on UNIX, or %GBASEDBTDIR%\etc\ixbar.<dbservernum>
for Dynamic Server 9.2 or later on Windows.) 


-43050	Copy of old emergency bootfile saved in <filename>.

Before regenerating the emergency boot file, onsmsync makes a copy of the
previous version. In case of catastrophic failure, this message tells you
the name of this copy so you can put it back in place before running
onsmsync again or starting a cold restore.

You cannot take any corrective action unless onsmsync fails. If onsmsync
fails to finish writing the new boot file, copy the named file back to the
original location.


-43051	Copy of old emergency bootfile failed.

Before regenerating the emergency boot file, onsmsync makes a copy of the
previous version. If this copying process fails, then this message is
produced. An operating-system error should be written in the activity log.

Try to correct the operating-system error. ON-Bar will continue to regenerate
the new emergency boot file.


-43052	Error parsing the emergency bootfile <filename>.

ON-Bar and onsmsync read information about backups from the emergency
boot file for various reasons. The emergency boot file might be corrupt.

ON-Bar and onsmsync will ignore corrupted backup entries while parsing
the emergency boot file. Contact GBase Technical Support.


-43060	The buffer <buffer-name> exceeded maximum allowed limit. Changing
buffer size to <size-value>.

Transport buffer size is limited by the X/Open Backup Services API
specification. ON-Bar is changing the buffer size to the maximum allowed
value.


-43061	BAR_MAX_BACKUP has been reduced to <size-value> to avoid
allocating more than SHMTOTAL KB.

The value of BAR_MAX_BACKUP multiplied by BAR_XFER_BUF_SIZE and
BAR_NB_XPORT_COUNT will exceed the amount of shared memory reserved by the
SHMTOTAL parameter in the ONCONFIG file. This ON-Bar process will use a
lower BAR_MAX_BACKUP value to avoid exceeding this boundary.

Either raise the amount of shared memory the database server is allowed to
use by increasing SHMTOTAL or lower the value of BAR_MAX_BACKUP,
BAR_XFER_BUF_SIZE, or BAR_NB_XPORT_COUNT.


-43062	Unable to read parameters from $GBASEDBTDIR/etc/$ONCONFIG
(UNIX) or %GBASEDBTDIR%\etc\%ONCONFIG% (Windows).

The ONCONFIG file is inaccessible. It might be missing or have incorrect
permission values, or the file contents might be corrupted.

Verify that an ONCONFIG file exists and that the permissions for it are
correct. For details, see the Administrator's Reference.


-43075	Unable to open file <filename>.

The file or its directory permissions prevent it from being created or opened.

Verify the permissions on the file and its directory.


-43076	Error <error-number> while reading data from the file
<filename>.

An error occurred while data was read from the file. The file might be
corrupted.


-43077	Unable to create <chunk-name>. There may not be enough space.

Not enough disk space is available for chunk creation.

Free some disk space or create a file for this chunk.


-43078	Open or close failed on file <filename>, errno = <error-number>.

An operating-system error prevents the file from being created or
opened.

Correct the error and retry your command.


-43079	Unable to open file <filename>.

The file or its directory permissions prevent it from being created
or opened.

Verify the permissions on the file and its directory.


-43080	Process <process-id> received signal <signal-number>.
Process will exit after cleanup.

An ON-Bar process received a signal. Someone might have intentionally
terminated the ON-Bar process with signal 2 (SIGINT), 3 (SIGQUIT), 9
(SIGKILL), or 15 (SIGTERM). ON-Bar is attempting to exit gracefully.

If the signal that terminated the ON-Bar process is not one of the
signals listed above, contact GBase Technical Support.


-43081	Closing unused end of pipe failed.

The system call to close the unused end of the backup and restore filter pipe failed.

This error is internal. Retry the operation and if it continues to fail,
contact GBase Technical Support.


-43082	Writing to the backup and restore filter failed with error <error number>.

Writing data to the backup and restore filter pipe failed with the given error.

Check the log of the filter program for possible errors.
If the cause of the error is still unclear, contact 
technical support  for your filter or GBase Technical Support.


-43090	Out of memory.

ON-Bar was unable to allocate more memory.

Reduce the number of processes that are running at the same time as ON-Bar,
if possible, or ask your system administrator either to increase your swap
space or to install more memory in your system.


-43091	Unable to attach to shared memory.

Unable to initialize a shared-memory connection to the database server.

Either the database shared memory has not been initialized or the maximum
number of users are already using the system.


-43092	Unable to set process group id.

If you are doing a parallel backup or restore, ON-Bar tries to use
setpgid() to group together all the processes. This error occurs if
setpgid() fails.

Retry the backup or restore. If it fails again, contact GBase Technical
Support.


-43100	No Storage Manager instances were defined in $ONCONFIG.

The list of available storage-manager instances has not been defined in the
ONCONFIG file. Backup and restore operations will be queued until a storage
manager is defined.

Define storage-manager instances in your ONCONFIG file.


-43101	Unable to create a session: <session-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43102	Unable to destroy a session: <session-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43103	Session <session-id> complete.

The specified backup and restore session has completed.


-43104	Session <session-id> complete with error <error-number>.

The specified backup and restore session has completed with an error.


-43105	Unable to register a new worker process: <process-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43106	Unable to deregister a new worker process: <process-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43107	Unable to get the next event: <event-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43108	Received an invalid event from the database server:
<event-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43109	Unable to create the logstream backup session: <session-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43110	Unable to start the logical log backup: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43111	Unable to get logical log backup data from the database
server: <server-name>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43112	Unable to close the logical log backup: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43113	Unable to commit the logical log backup: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43114	Unable to create the storage space backup session:
<session-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43115	Unable to start the storage space backup: <storage-space>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43116	Unable to get storage space backup data from the database
server: <server-name>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43117	Unable to close the storage space backup: <storage-space>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43118	Unable to commit the storage space backup: <storage-space>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43119	Unable to create the storage space restore session:
<session-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43120	Unable to start the storage space restore: <storage-space>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43121	Unable to write storage space restore data to the database
server: <server-name>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43122	Unable to close the storage space restore: <storage-space>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43123	Unable to commit the storage space restore: <storage-space>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43124	Unable to create the logical log restore session:
<session-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43125	Unable to start the logical log restore: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43126	Unable to write logical log restore data to the database
server: <server-name>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43127	Unable to close the logical log restore: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43128	Unable to commit the logical log restore: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43129	Unable to create the salvage logs session: <session-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43130	Unable to start logical log salvage: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43131	Unable to get logical log salvage data from disk: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43132	Unable to commit the logical log salvage: <unique ID>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, GBase contact Technical Support.


-43133	Unable to open logical log placement: <log-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43134	Unable to close logical log placement: <log-id>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43135	Error suspending session <session-id>: <error-number>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43136	Error resuming session <session-id>: <error-number>.

An error occurred in the database server.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43137	The log backup may have made at least 1 blobspace unrestorable.

A log was backed up when a blobspace was down. The transactions in the logs
(transactions that involve TEXT or BYTE data) might not be restorable.

You might have to re-create the TEXT or BYTE data after the restore is
complete.


-43138	Unable to allocate the transport buffer: <text>.

An error occurred when the database server tried to allocate the transport
buffers.

Note all circumstances, save a copy of the ON-Bar activity log and
database server message log, and contact GBase Technical Support.


-43139	Unable to free the transport buffer: <buffer-name>.

An error occurred when the database server tried to free the transport
buffers. This error might occur in onbar workers if the database server
is brought down. 

In all other cases, note the circumstances, save a copy of the ON-Bar
activity log and database server message log, and contact GBase Technical 
Support.


-43140	Due to the previous error, logical restore will not be
attempted.

An error occurred in the physical restore. ON-Bar does not attempt a
logical restore to give the user a chance to fix the problem. If the
physical restore was cold, the database server is left in fast recovery
mode.

You have the following two choices:

1) Fix the problem and finish the restore with onbar -r or 
   onbar -r <failed_space>. ON-Bar re-attempts to restore the failed
   storage space.

2) Finish the restore with onbar -r -l. ON-Bar performs a logical
   restore but leaves off-line any storage spaces that are not yet
   physically restored.


-43150	Must restore logical logs from <timestamp> or later.

The user wants to stop the restore at too early a logical log. A storage-space
backup occurred after the log that the user specified.

Retry the restore up to the specified logical log or later.


-43151	Cannot warm restore critical media: <space-id>. Skipped restore
of this space.

Critical media (root dbspace or any dbspace that contains a logical or
physical log) cannot be restored if the database server is online. This
space will be skipped.


-43152	Begin cold level <level-number> restore <value-2> (Storage
Manager copy ID: <value-3> <value-4>).

This is an ON-Bar progress message.


-43153	Completed cold level <level-number> restore <value-2>.

This is an ON-Bar progress message.


-43154	Begin fake backup.

This is an ON-Bar progress message.


-43155	Completed fake backup.

This is an ON-Bar progress message.


-43156	Fake backup failed. <error-number>

Contact GBase Technical Support.


-43157	Begin level <level-number> backup <value-2>.

This is an ON-Bar progress message.


-43158	Completed level <level-number> backup <value-2> (Storage Manager
copy ID: <value-3> <value-4>).

This is an ON-Bar progress message.


-43159	Begin salvage for log <unique ID>.

This is an ON-Bar progress message.


-43160	Completed salvage of logical log <unique ID> (Storage Manager copy ID:
<value-2> <value-3>).

This is an ON-Bar progress message.


-43161	<Number-of-spaces> off-line blobspaces (<space-ids>) were found
while salvaging logical logs.

The logical-log backup of changes to a blobspace requires the blobspace to
be online. Any blobspace that is off-line during a logical-log salvage
(which means the blobspace was off-line before the database server failed)
is no longer restorable using ON-Bar.

Re-create the blobspace and reload it as recommended in the Administrator's
Guide.


-43162	Begin warm level <level-number> restore <value-2> (Storage
Manager copy ID: <value-3> <value-4>).

This is an ON-Bar progress message.


-43163	Completed warm level <level-number> restore <value-2>.

This is an ON-Bar progress message.


-43164	Begin backup logical log <unique ID>.

This is an ON-Bar progress message.


-43165	Completed backup logical log <unique ID> (Storage Manager copy ID:
<value-2> <value-3>).

This is an ON-Bar progress message.


-43166	Begin restore logical log <unique ID> (Storage Manager copy ID:
<value-2> <value-3>).

This is an ON-Bar progress message.


-43167	Completed restore logical log <unique ID>.

This is an ON-Bar progress message.


-43168	ON-Bar suspended the logical restore on log <value-1>
(expected to restore to <value-2>).

ON-Bar was able to restore up through log file x.

To restore all data, however, you also need to restore log files x + 1
through y. Query your storage manager to find out if the backups of these
log files exist and if they are restorable. If the log files are not
restorable or you do not want to restore them, issue the following commands
to force the logical restore to end:

   onmode -k; oninit

Otherwise, issue onbar -r -l to resume the logical restore.


-43169	You must restore all critical storage spaces for a cold restore.

A cold restore was attempted that did not include the critical storage spaces.

Try the restore again, but include the root dbspaces and all other critical
dbspaces, if any.


-43170	ON-Bar could not find the proper log from which to begin the
logical restore.

Either no logical-log restore is needed or a logical-log backup is not
restorable.

If the database server is not in quiescent or online mode, contact
GBase Technical Support.


-43171	Cannot restart a warm logical restore.

ON-Bar cannot restart a logical restore with the database server in online
or quiescent mode.

Bring down the database server and restart the restore.


-43172	Completed external restore.

This message is an ON-Bar progress message.


-43173	External restore failed. 

An error occurred in the database server.

Refer to the ON-Bar activity log or database server message log for
more information. If the cause of the error is still unclear, contact
GBase Technical Support.


-43174	<percent-number> percent of <dbspace-name> has been backed up.

This message provides progress feedback for backup as a percentage of dbspace
size.


-43175	<percent-number> percent of <dbspace-name> has been restored.

This message provides progress feedback for restore as a percentage of
dbspace size.


-43176	<buffer-quantity> buffers of <value-2> have been restored.

This is an ON-Bar progress message that reports the number of buffers
restored.


-43177	Physical restore complete. Logical restore required before work
can continue. Use 'onbar -r -l' to do logical restore.

This restore was a physical-only restore, or the logical restore failed.

Perform a logical restore. 


-43178	The data returned from the Storage Manager for restore is
incomplete.

The amount of data in the buffer that the storage manager returned for
the restore is not a multiple of the database server page size
(2 kilobytes, 4 kilobytes, or 8 kilobytes, depending on your system
and configuration). A restore requires complete pages of data.

Check your storage manager for possible errors. Verify that all the
storage media are complete and usable and that all the storage devices
are functional and online.


-43179	Continuous logical log backup sessions cannot be destroyed. Use
onbar off "Log backup <coserver-id>".

An attempt to destroy a continuous logical-log backup session (Log backup
<coserver id>) was made. This action is not allowed.

If you need to stop a logical-log backup session for some reason, use
'onbar off "Log backup <coserver-id>"', in which <coserver-id> is the number
of the coserver (1, 2, 3, and so on), to temporarily suspend the session.
You can use 'onbar on "Log backup <coserver-id>"' to resume the session when
the problem is resolved. Do not use 'onbar -d "Log backup <coserver-id>"'
to destroy the session.


-43180	One of the spaces externally restored on coserver <coserver-id>
was backed up after <time-stamp>.

ON-Bar has determined the most recent time to which all coservers can be
restored and has reported the internal global time stamp in the error
message. One of the storage spaces that was externally restored on the
named coserver was backed up after that time. Hence a logical restore
cannot return all storage spaces to a consistent state.

Use the ctime() function to convert the global time stamp to a
clock time. Then check that all the external backups used in the
external restore of data on the named coserver were made before that
time. Any space that was restored from too recent a backup needs to be
re-restored from an older backup. Then try the ON-Bar external restore
command again.


-43181	Completed logical restore.

This message is an ON-Bar progress message.

No action is required.


-43182	Completed whole-system restore.

This message is an ON-Bar progress message.

No action is required.


-43183	Logical logs will not be backed up / salvaged because LTAPEDEV
value is <value>.

If the LTAPEDEV value in your ONCONFIG file is /dev/null on UNIX or 
NUL on Windows, logical-log backups are not performed. You set these
special values to tell the database server and ON-Bar not to perform
any logical-log backups. Data in the logical logs cannot be restored.

Set the LTAPEDEV value to something other than /dev/null on UNIX or NUL
on Windows or to blank if you want to perform logical-log backups.
If you do not want any logical-log backups, you can restore only from
whole-system backups, performed with the following command:

   onbar -b -w

Also, you must restore with the following command:

   onbar -r -w -p


-43184	The transport buffer size has changed since the backup.
Changing buffer size to <value> for restore.

A space or a logical log must be restored using the same transport buffer
size as was used when the space or log was backed up.  ON-Bar automatically
detects that the buffer size has changed and uses the buffer size from
the backup.


-43185	A change in physical configuration has occurred. A level-0
backup of <space_name> is being performed.

The physical configuration of the database server has changed. For
example, a storage space, chunk, or logical-log file has been added or
dropped, or the physical log has been moved. To guarantee a successful
restore, a level-0 backup of the affected space and the root dbspace
is being performed now.

No action is required.


-43186	The logical log is full on one or more coservers. ON-Bar's
attempt to back up these logs failed. A logical-log backup is needed.

The logical log is full on one or more coservers. Processing will be
blocked until these logical logs are backed up.

Perform a logical-log backup. Processing will continue after this
backup is complete.


-43187	ON-Bar received an error/signal. The onbar session will
abort after cleanup.

If any of the ON-Bar processes encounters a non-fatal error and if
BAR_RETRY is set to ABORT(0), the parent process will not create any new
child processes. If the parent process receives a signal, the process
ignores the BAR_RETRY value and then aborts. In both cases, the ON-Bar
child processes that are running will continue to completion.

Rerun the backup or restore command.


-43189	Logical Logs cannot be restored because LTAPEDEV value is
<value>.

If the LTAPEDEV parameter value in your ONCONFIG file is /dev/null (UNIX)
or NUL (NT) or blank, logical log restore cannot be performed.  These are
special values that you set to tell the server and ON-Bar that
log backups are not desired. Data in the logical log cannot be restored.

Set the LTAPEDEV parameter value to something other than /dev/null (UNIX)
or NUL (NT) or blank if logical log restores are desired.  If logical
log restores are not desired use the -w and -p options with ON-Bar
commands to perform whole system, physical-only restore.


-43190	WARNING: Storage space names with path will not be supported
from next release onwards. Correct this in <filename>.

Only space (dbspace, blobspace, and so forth) names should be specified to ON-Bar
in a file or command line. ON-Bar will backup all the chunks belonging
to the spaces.

Correct the file to specify storage space name without path prefixing.


-43194	WARNING: XBSA connect to storage manager failed.
Continuing external restore.

Either the storage manager is not running or it has not been installed and
configured.

For pure external physical-only restore, no storage manager is needed.
Therefore, the external restore is continued at this point.
However, a storage manager may be necessary later if logical logs need
to be restored from backup media.


-43200	Shared memory not initialized.

The database server is not running.

Start the database server. For instructions, see the Administrator's
Guide.


-43201	The database server has crashed or been shutdown. Exiting...

The database server has failed or has been shut down while one or more
ON-Bar worker processes are still running. The ON-Bar worker processes
will be automatically shut down.


-43202	Unable to determine mode of all coservers.

It was not possible to determine what mode each coserver is in (online,
quiescent, off-line, or microkernel).

Verify that all coservers are running and that the coservers are
communicating with each other properly.


-43203	Not all coservers are in a compatible mode.

Not all coservers are in the same mode. Each coserver must be in a mode
compatible to all the others. For example, all the coservers must be in
microkernel mode to perform a cold restore. On-line and quiescent modes are
compatible, so a backup or warm restore can be performed as long as all
coservers are in one of these two modes.

Change all coservers to the same mode or to compatible modes.


-43204	ON-Bar is waiting for the database server to exit fast recovery
mode.

After finishing the logical restore phase of a cold restore, the database
server enters fast recovery mode. ON-Bar waits for fast recovery to complete
before cataloging the activity of the cold restore.


-43205	Failed to connect to the sysmaster or sysutils database.
Wait until these databases are created and try again.

Either the sysmaster or sysutils database has not been created.

Monitor the database server message log until the following messages
appear:
 
   "'sysmaster' database built successfully" 
   "'sysutils' database built successfully"

Then retry your command.

If one or both of these messages fail to appear, contact GBase Technical
Support.


-43206	An attempt to change the database server operating mode
failed: <server-name>.

An error occurred during an attempt to change the database server operating
mode.

Check the database server message log for errors.


-43207	Unable to open connection to database server: <server-name>.

The database server is in an incorrect operating mode.

Bring the database server to the correct mode. For a backup, the
database server should be in online or quiescent mode. For a warm
restore, the database server should be in online, quiescent, backup,
or recovery mode. For a cold restore, the database server should be
off-line for GBase Database Server 9.2x or later and in microkernel
mode for GBase Extended Parallel Server. To change the database
server mode, use the onmode or oninit command.


-43208	Fatal error initializing ASF; asfcode = <asfcode_value>

An error occurred during initialization of the ASF layer.

To determine the cause of the error, refer to the ASF error codes
and then rerun the backup or restore command.


-43210	ON-Bar failed to initialize the XCC communications mechanism
(<mechanism-id>).

An error occurred in an attempt to initialize communications between
coservers.

Refer to the database server message log for more information. If the
cause of the error is still unclear, contact GBase Technical Support.


-43211	The onbar process is waiting for onbar_m processes to connect
at <address-name>.

In a cold restore on GBase Extended Parallel Server or GBase
Dynamic Server with Advanced Decision Support and Extended Parallel options,
the ON-Bar process that the user started launches onbar_m processes on
various nodes of the MPP to collect data required to perform the cold
restore. Each onbar_m process needs to connect to the ON-Bar process using a
communications address that the ON-Bar process devised. This message names
the address.

If you are starting onbar_m by hand, type the address that the message named
on the onbar_m command line.


-43212	Waiting for onbar_m processes to connect...

Several onbar_m processes have just started, and ON-Bar is waiting for all
of them to come up.


-43213	All onbar_m processes have connected.

This is an ON-Bar progress message.


-43214	The onbar_m process started on node <node-name> failed with
status <status-id> : <status-information>.

One of the onbar_m processes failed to connect to ON-Bar.

If this message occurs once, try to start an onbar_m process on the named
node by hand as user gbasedbt. If this message occurs repeatedly, contact
GBase Technical Support with the status information in the 
error message and with the contents of your ONCONFIG file.


-43215	The onbar_m process on node <node-name> sent an unexpected
message (<message-1> instead of <message-2>).

This message indicates either an error in the ON-Bar software or an onbar_m
process that did not start correctly.

Contact GBase Technical Support.


-43216	The ON-Bar process received a polling error from XCC
(<value-1> : <value-2>).

An error occurred in inter-coserver communications.

If this message occurs once, try to start the cold restore again. If
this message occurs repeatedly, contact GBase Technical Support
with the status information in the error message and with the contents
of your ONCONFIG file.


-43217	The ON-Bar process failed to send an XCC message
(<value-1> : <value-2>).

An error occurred in inter-coserver communications.

If this message occurs once, try to start the cold restore again. If
this message occurs repeatedly, contact GBase Technical Support
with the status information in the error message and with the contents
of your ONCONFIG file.


-43218	The ON-Bar process failed to receive an XCC message
(<value-1> : value-2).

An error occurred in inter-coserver communications.

If this message occurs once, try to start the cold restore again. If
this message occurs repeatedly, contact GBase Technical Support
with the status information in the error message and with the contents
of your ONCONFIG file.


-43219	A rename chunk restore failed. Server is not in off-line mode.

Restore with chunk rename can only be done as cold restore when the server
is off-line.

To do a restore with chunk rename, bring the server off-line and do
a cold restore.


-43230	The problem just reported is not fatal; processing will continue.

ON-Bar just reported a warning or error in the activity log. However, this
problem is not fatal. ON-Bar will continue processing, either retrying or
using a different algorithm to complete the task. Other unrecoverable errors
might occur later.

Watch for further errors. Be aware that further processing may take longer
than expected.


-43231	An unexpected error occurred: <value-1> <value-2>.

Several possible causes exist for this error, including operating-system
failures, version incompatibilities, and software errors.

Verify that the versions of the database server, ON-Bar, and the storage
manager are compatible. For a list of compatible versions, refer to the
GBase Backup and Restore Guide, release notes, machine notes, or storage
manager documentation.

If the versions are compatible, try to stop the backup and restore process
or processes and restart them.

If the error persists, note all circumstances, save a copy of the ON-Bar
and database server message logs, and contact GBase Technical Support.


-43232	The error just encountered may be transitory. ON-Bar is
retrying the last statement.

ON-Bar just reported a warning or error. This problem, however, might be
caused by another process and might clear up after the other process
continues. ON-Bar will automatically retry the failed statement.

Monitor for further errors. If the automatic retry fails, retry the
backup or restore command.


-43233	Linked list operation failed <operation>.

A linked list operation failed.

This error is internal. Retry the operation and if it
continues to fail, contact GBase Technical Support.


-43234	New BAR_DEBUG level recognized:  old value was <value>, new
value is <value> .

The BAR_DEBUG parameter in the ONCONFIG file has changed values or
debug level is specified on command line. The new debug level value
will be used for further tracing.


-43240	Process <process-id> successfully forked.

This is an ON-Bar progress message.


-43241	Process <process-id> completed.

This is an ON-Bar progress message.


-43242	The ON-Bar process <process ID> exited without returning an exit code.

An ON-Bar child process was started and exited, but the parent process could
not find its exit code.  This situation can occur if too many child processes
exit at the same time.

ON-Bar proceeds as if this child process were successful and cataloged its
activity properly. Check the activity log for any evidence whether this process
encountered an error. Make sure the bar_action and bar_object tables in the
sysutils database correctly reflect the success or failure of this process.


-43243	The process <process ID> is participating in a backup/restore
session but is suspended.

The database administrator, computer operator, or storage manager sent SIGSTOP
or SIGTSTP, usually a CTRL-Z or CTRL-Y, to this process. Presumably this
action was taken to halt processing temporarily during resolution of a problem.

Send SIGCONT to this process (kill -CONT <process ID>) to allow the process to
complete its backup or restore, SIGTERM to stop this particular process, or
SIGQUIT to end the whole backup or restore session.


-43244	All onbar processes participating in the backup/restore session are
suspended.

The database administrator, computer operator, or storage manager sent
SIGSTOP or SIGTSTP, usually a CTRL-Z or CTRL-Y, to all of the processes
involved in this backup or restore session. Presumably this action was
taken to halt processing temporarily during resolution of a problem.

Use the ps system command to find a list of all ON-Bar processes. Send
SIGCONT to each of them (kill -CONT <process ID>) to allow each process
to complete its backup or restore, SIGTERM to stop a particular process,
or SIGQUIT to one of the processes to end the whole backup or restore
session.


-43245	ON-Bar could not start another child process.

ON-Bar encountered an error starting another process to back up or restore
data in parallel.  A lack of sufficient memory or the user having too many
processes running at the same time usually causes this situation.

Increase the amount of real memory or swap space, end any unnecessary
processes, or reduce BAR_MAX_BACKUP value in your ONCONFIG file.


-43246	The ON-Bar process <process ID> exited with a problem (exit code
<value> (0x<hex value>), signal <signal number>).

An ON-Bar child process encountered an error while performing its backup or
restore. The child process returned an error (the exit code) to its parent.
If the exit code has the value 141 (0x8d), then the child process exited
because it received an operating-system signal, reported by this message.

If the exit code reported in the message is non-zero, then look at the list
of ON-Bar exit codes in the documentation or at the ON-Bar activity log to
find the cause of the problem. If the signal number is non-zero, look at
the documentation for your operating system to see what might have caused
this problem.


-43247	<process ID> complete, returning <status-value> (0x<hex value>).

An ON-Bar process has completed its processing and is exiting.

If the process returned 0, then no further action is required. If the
process returned a nonzero value, then check the activity log for errors and
take action appropriate for those errors.


-43255	Successfully connected to Storage Manager.

This is an ON-Bar progress message.


-43256	Version <version-1> of the XBSA shared library is not
compatible with version <version-2> of ON-Bar.

Either the XBSA shared library that the storage manager vendor
provided has not been validated by GBase, or an error occurred during
the installation of ON-Bar or the storage manager application.

Look at the contents of the sm_versions file in $GBASEDBTDIR/etc
or %GBASEDBTDIR%\etc. Make sure that the data matches the version of
your storage manager and confirm that this version of the storage
manager has been certified with this version of ON-Bar. Consult your
storage-manager documentation for the correct storage manager version
to put into the sm_versions table in the sysutils database.


-43257	Could not open XBSA library <library-name>.

The shared library does not exist, it has a bad format, or you do not have
permission to read it.

Make sure that the shared library for the Storage Manager was installed
properly and that the ONCONFIG file has the correct value for BAR_BSALIB_PATH.


-43258	ON-Bar could not get logical log <unique ID> from the Storage
Manager.

The backup of this logical log file is missing.

Query your storage manager to find out if the backup of this log file
exists and if it is restorable.


-43259	Could not open XBSA library <library-name>, so trying default
path.

The shared library does not exist, it has a bad format, or you do not have
permission to read it.

Make sure that the shared library for the storage manager was installed
properly and that the ONCONFIG file has the correct value for BAR_BSALIB_PATH.


-43260	ON-Bar was unable to get backup record of <space-name> from
storage manager.

The storage manager does not have a backup record for the dbspace or
blobspace.

For more information, contact the storage manager vendor or GBase Technical
Support.


-43261	BAR_BSALIB_PATH is undefined; trying default path.

Either a line in the ONCONFIG file contains BAR_BSALIB_PATH with no value,
or a problem occurred reading the ONCONFIG file.

Verify that the format of the ONCONFIG file is correct. Also make sure that
either BAR_BSALIB_PATH is assigned a correct value or is not specified in
the file at all.


-43262	The wrong version of <object-name> was returned from the
Storage Manager.

The storage space or log file returned by the storage manager for
restore is not the object requested by ON-Bar.

Query your storage manager to find out if the correct backup of this
storage space or log file exists and if it is restorable.


-43263	XBSA Error: (<error-number>) Active object does not exist.
Attempt to deactivate it failed.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43264	XBSA Error: (<error-number>) A system error occurred. Aborting XBSA session.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43265	XBSA Error: (<error-number>) Attempt to authorize <user-id>
failed.

Verify that the username is gbasedbt or root or is a member of the
bargroup group.


-43266	XBSA Error: (<error-number>) Invalid XBSA function call
sequence.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43267	XBSA Error: (<error-number>) Invalid XBSA session handle
<handle-id>.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43268	XBSA Error: (<error-number>) XBSA buffer is too small for
the object.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43269	XBSA Error: (<error-number>) Description of the object
exceeds the maximum allowed value: <maximum-value>.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43270	XBSA Error: (<error-number>) The database server name
exceeds maximum allowed size <size-value>.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43271	XBSA Error: (<error-number>) The new security token name is
invalid.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43272	XBSA Error: (<error-number>) Invalid vote value: Must be
BSAVoteCOMMIT or BSAVote_ABORT.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43273	XBSA Error: (<error-number>) Invalid environment keyword.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43274	XBSA Error: (<error-number>) That object already exists.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43275	XBSA Error: (<error-number>) A new security token must be
created.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43276	XBSA Error: (<error-number>) Backup object does not exist in
Storage Manager.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43277	XBSA Error: (<error-number>) Exceeded available resources.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43278	XBSA Error: (<error-number>) A DataBlock pointer is
required.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43279	XBSA Error: (<error-number>) An object name is required.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43280	XBSA Error: (<error-number>) Unable to access NULL pointer.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43281	XBSA Error: (<error-number>) Rule ID is required.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43282	XBSA Error: (<error-number>) The object is not empty.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43283	XBSA Error: (<error-number>) This object has not been
backed up.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43284	XBSA Error: (<error-number>) Object information data exceeds
maximum allowed size <size-value>.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43285	XBSA Error: (<error-number>) Object name exceeds maximum
allowed size <size-value>.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43286	XBSA Error: (<error-number>) Operation is not authorized
for <user-id>.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43287	XBSA Error: (<error-number>) A value for the old security
token is required.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43288	XBSA Error: (<error-number>) The security token has expired.
Create a new one.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43289	XBSA Error: (<error-number>) The transaction was aborted.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43290	XBSA Error: (<error-number>) A quote is missing from an
environment keyword.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43291	XBSA Error: (<error-number>) A username cannot be deleted
while it owns objects.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43292	XBSA Error (<value-1>): An unspecified XBSA error has
occurred: <value-2>.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43293	XBSA Error: (<error-number>) A query or object retrieval
tried to get more data than exists.

An error occurred in the storage manager application.

Refer to the storage manager message log (or equivalent) for more
information. If the cause of the error is still unclear, contact your
storage manager technical support. If you are using the GBase Primary Storage
Manager, contact GBase Technical Support.


-43294	ON-Bar was unable to get the backup record of logical log <unique ID>
from the storage manager.

The storage manager issued an error when finding a backup record
for the logical log.

For further information, contact the storage manager vendor
or GBase Technical Support.


-43295	WARNING: BAR_TIMEOUT unable to initialize connection to Storage
Manager.

The storage manager may not be running or there may be storage manager
configuration problems.

Refer to the storage manager message log (or equivalent) for more information.
If the cause of the error is still unclear, contact your storage manager
Technical Support. If you are using the GBase Primary Storage Manager, contact
GBase Technical Support.


-43296	WARNING: BAR_TIMEOUT Storage Manager Progress may be stalled.

The current command has exceeded the BAR_TIMEOUT value. This may indicate
a normal transfer of a large data set or it may mean the storage manager
needs attention.

Check to make sure the storage manager is still running and that data is still
being transfered to/from the storage manager.
Refer to the storage manager message log (or equivalent) for more information.
If the cause of the error is still unclear, contact your storage manager
Technical Support. If you are using the GBase Primary Storage Manager, contact
GBase Technical Support.


-43297	BAR_BSALIB_PATH in ONCONFIG is not an absolute pathname.

The absolute path of the XBSA library was not specified for the BAR_BSALIB_PATH
parameter in the ONCONFIG file.

Modify the BAR_BSALIB_PATH parameter in the ONCONFIG file by specifying
the absolute path of the XBSA library.


-43300	WARNING: BAR_TIMEOUT: The backup and restore filter program progress might be stalled.

The current command has exceeded the amount of time specificed by 
the BAR_TIMEOUT configuration parameter. This  can indicate a slow 
filter program, the transfer of a large data set, or it  can mean 
that the storage manager needs attention.
.ACTION
Make sure the storage manager is still running and that data is still
being transfered  to or from the storage manager. 
Review the storage manager message log or the log file for the filter.
If the cause of the error is still unclear, contact your storage manager
or Technical Support for your filter. If you are using the GBase
Storage Manager, contact GBase Technical Support.


-43303	Creating the backup and restore filter process <filter program> failed with error code <error number>.

ON-Bar attempted to create a filter process for running the configured 
filter program. The process creation for execution of the filter 
program failed with the given error code.
.ACTION
Check that the BACKUP_FILTER and RESTORE_FILTER configuration parameters 
are configured correctly in the onconfig file. If the cause of the 
error is still unclear, contact Technical Support  for your filter.

-43305	<object-name> (copy ID <id-1> <id-2>) not expired even though it
matches the expiration policy.

The named object matches the expiration policy named on the command line
with the -g, -i, or -t option. The object cannot be deleted, however, because
it is needed to allow the restore of another object that does not match the
expiration policy.

This message is informational.


-43306	Whole system backup from <timestamp> not expired even though it
matches the expiration policy.

The whole-system backup started at the described time matches the expiration
policy named on the command line. The backup cannot be deleted, however,
because it is needed to allow the restore of another whole-system backup that
does not match the expiration policy.

This message is informational.


-43307	Expired <object-name> (copy ID <id-1> <id-2>), backed up
<timestamp>.

The named object has been expired as requested.


-43308	Expired <object-name> (copy ID <id-2> <id-3>), backed up
<timestamp>; restore may not be possible.

The named object has been expired as requested. Normally onsmsync would not
have removed it, but the -O option was passed on the command line. If the
object is a storage space, ON-Bar cannot determine if it can restore that
space from remaining data in the sysutils database. If the object is a logical
log, ON-Bar cannot determine if any subsequent restore attempts will fail.

If the object can be re-created in the storage manager and you think you may
need to restore this object with ON-Bar, then follow the procedures for
re-creating the object in the storage manager. Then contact GBase Technical
Support for assistance.


-43309	Expired whole system backup from <timestamp>; restore may not be
possible.

The whole-system backup started at the described time has been expired as
requested. Normally onsmsync would not have removed it, but the -O option was
passed on the command line. ON-Bar cannot determine if it can restore from
the remaining data in the sysutils database.

If the objects in this whole-system backup can be re-created in the storage
manager and you think you may need them to restore with ON-Bar, then follow
the procedures for re-creating them in the storage manager. Then contact
GBase Technical Support for assistance.


-43310	Could not update the sysutils database - run onsmsync -b when
the database server is quiescent or online.

Either a log salvage was run as a distinct step (onbar -b -l -s), ON-Bar
encountered an error cataloging an action but successfully updated the
emergency boot file, or onsmsync was run when the database server was
off-line or otherwise unavailable. Therefore the sysutils database is
inconsistent with the storage manager and emergency boot file.

Bring the database server online (possibly including a physical and
logical restore) and run onsmsync -b.


-43319	Exiting without expiring any backup object. All the storage spaces
met the expiration policy or storage spaces were not backed-up using ON-Bar.

After applying the expiration policies specified by the -g option, the onsmsync
list of objects contains only logical logs. The list does not contain space
backups.

Use the -t or -i option to expire logical log backups.


-43320	Begin backup verification of level <level-number> for <object-id>
(Storage Manager copy ID: <copy-id1> <copy-id2>).

This message is an ON-Bar progress message.


-43321	<percent-number> percent of <verification> has been verified.

This message provides progress feedback for the backup verification as a
percentage of dbspace size.


-43322	Completed level <level-number> backup verification successfully.

This message is an ON-Bar progress message.


-43323	ERROR: Unable to create the physical check session:
<session-number>.

This error occurs during backup verification.

Before you call Technical Support, examine the message log files for
the database server, ON-Bar, and archecker.


-43324	ERROR: Unable to open the physical check errtxt: <error-number>.

This error occurs during backup verification. It indicates a problem running
the archecker process. 

Check permissions and disk space for the archecker temporary files. For
these file locations, see $GBASEDBTDIR/etc/ac_config.std or
%GBASEDBTDIR%\etc\ac_config.std. Before you call Technical
Support, examine the message log files for the database server,
ON-Bar, and archecker.


-43325	ERROR: Unable to write the physical check errtxt: <error-number>.

This error occurs during backup verification. It indicates a problem
running the archecker process.

Check permissions and disk space for the archecker temporary files. For
these file locations, see $GBASEDBTDIR/etc/ac_config.std or
%GBASEDBTDIR%\etc\ac_config.std. Before you call Technical
Support, examine the message log files for the database server,
ON-Bar, and archecker.


-43326	ERROR: Unable to close the physical check errtxt: <error-number>.

This error occurs during backup verification. It indicates either a problem
running the archecker process or a corrupt backup.

Check permissions and disk space for the archecker temporary files. For
these file locations, see $GBASEDBTDIR/etc/ac_config.std or
%GBASEDBTDIR%\etc\ac_config.std. Before you call Technical
Support, examine the message log files for the database server,
ON-Bar, and archecker.


-43328	Failure in archecker process execution status.

An error occurred during Level <value> backup verification.

Check permissions and disk space for the archecker temporary files. For
these file locations, see $GBASEDBTDIR/etc/ac_config.std or
%GBASEDBTDIR%\etc\ac_config.std.  Before you call Technical Support,
examine the message log files for the database server, ON-Bar, and archecker.
If the AC_CONFIG environment variable is not set and there is no ac_config.std
file in $GBASEDBTDIR/etc or %GBASEDBTDIR%\etc, then look at the msg.log file
generated by archecker in the directory where ON-Bar is executed.


-43362	Begin displaying logical log <log-id>. (Storage Manager copy ID:
<value-2> <value-3>).

This is an ON-Bar progress message.


-43363	Completed displaying logical log <log-id>.

This is an ON-Bar progress message.


-43364	No logical logs to display.

You are trying to display one or more logical logs but none of those you
specified have been archived using ON-Bar or the emergency boot file
might be corrupted.

Make sure that the emergency boot file has the log backup information.


-43365	Unable to display logical log : <unique ID> .

There could be a problem in constructing a block of log records
which shall be displayed using the callback function. The logical
log file you are displaying may be corrupted.


-43373	Sysutils database will be updated when server exits fast recovery.

ON-Bar tries to update the sysutils database while the database server
is in fast recovery. When the database server exits fast recovery, the
sysutils database is updated.

Place the database server in quiescent or online mode, then run the
onsmsync -b command. This command generates the emergency boot file.


-43376	Unable to convert datetime to string: <date>.

The date string could not be interpreted because the format
used did not match the format expected by ON-Bar.

Check for the values of environment variables related to internationalization
or localization (such as CLIENT_LOCALE and GL_DATETIME).  If the environment
is set for localization, then make sure the date specified matches the local
format for dates. If no appropriate environment variables are set, then make
sure the date was specified in the standard SQL date format.


-43380	Deadlock detected. Wait a few seconds for the lock to clear and try
again.

Two processes attempted to lock the same sysutils database table or row at
the same time resulting in a deadlock.

Wait a few seconds for the other process to complete and release the lock
and try again.


-43381	The logical logs are full. ON-Bar might encounter errors writing
to the sysutils database.

The logical logs were filled while ON-Bar was running. ON-Bar performs
database transactions when it writes a record of what it has done
to the ON-Bar catalogs in the sysutils database. Therefore, the
work performed by the current ON-Bar process is not recorded in
the sysutils database. However, it is recorded in the emergency
boot file.

If by the end of the backup or restore, the object still was not cataloged,
use onsmsync to update the data.


-43385	Restoring <storage space> even though it is online.

ON-Bar by default does not restore spaces if they are online.
To get this message, the -O option must have been used on the
onbar command line to override internal checks.

No action is necessary. ON-Bar will restore the space.


-43388	The maximum allowed number of storage spaces per ON-Bar command has
been exceeded.

There is a limit to how many storage spaces ON-Bar may process in a whole
system backup or restore. This number varies from platform to platform.
Non-whole system backup or restore does not have such a limit.

If possible, retry the backup or restore without the whole system option.
If a whole system restore is required, consider reducing the number of
storage spaces in your installation.


-43389	<storage space> does not have a level <value-1> backup.
A level <value-2> backup will be performed by default.

An incremental backup requires that a backup of the previous level exists.
For example, there must be at least one level 0 backup of the object before
a level 1 backup of it may be performed.  Both a level 0 and a level 1
backup must exist before a level 2 backup may be performed.


-43391	Skipped backup/restore of space <storage space>.

The specified storage space was a temporary space, the space was online,
or it is the root dbspace and the database server is in Fast Recovery
mode and does not need a restore.

No corrective action is necessary.


-43392	One or more BLOBspaces are down. Log backup has been aborted.

A BLOBspace is down. Backing up or salvaging the logical logs would
make it impossible to restore this BLOB in the future.

Bring all BLOBspaces online and retry the logical log backup or salvage.


-43393	Storage space <storage space> is down and cannot be backed up.

Only storage spaces that are online can be backed up.

Bring the storage space online and attempt the backup again.


-43394	Storage space <storage space> is not down so it will not be restored.

Only storage spaces that are off-line need to be restored. This storage space
will not be restored, but restore of any other storage spaces will be
attempted.


-43395	A log backup is already running. Can't start another.

This occurs when several onbar processes try to back up the logical
logs at the same time. One of the causes might be that the program
defined by the ALARMPROGRAM parameter in your ONCONFIG file calls
onbar to do log backups and is executed by the database server while another
onbar process is also trying to perform a log backup. Another possible
cause is that multiple onbar command-line calls were made to perform
restores, and they are interfering with each other.

No action is required. The first log backup that is started will
back up all of the used log files.


-43399	ERROR: No response was received from the database server.
Aborting ON-Bar.

There was no response from the database server. The database server
may not be running properly and may have a problem.

Determine what is wrong with the database server, repair it, and
retry the backup or restore.


-43400	ondblog usage.

ondblog <new mode> [-f <filename>] [<database list>]

    new mode:

        buf     - Change database to buffered mode.
        unbuf   - Change database to unbuffered mode.
        nolog   - Change database to no logging.	(not in AD/XP)
        ansi    - Change database to be ANSI-compliant
        cancel  - Cancel logging request.

    -f <filename>   File containing list of databases for logging change.

    <database list> List of databases for logging change.

This message displays after an incorrect entry of the ondblog command.

Revise the command and try again.


-43402	ERROR: Invalid serial number. Consult your installation
manual.

The serial number for ondblog is invalid.

Ask your System Administrator to re-install ondblog.


-43421	Logging mode <log-mode> is not available in this edition of the
database server.

The named logging mode is available in some but not all versions of
GBase servers.

Find an appropriate logging mode that is available. Check the release
notes or your Administrators Guide for supported logging modes.


-46007	Cannot perform Java-to-SQL type mapping for type (<type name>).

This error can occur if you use the SQL data type, such as FLOAT, in the
EXTERNAL NAME clause of the CREATE FUNCTION statement.

Use the Java data type, not the SQL data type, in the EXTERNAL NAME
clause.


-46103	Error loading Java UDR class (<jar filename>). Verify
the three part name (<dbname.username.jarid>) of the installed jar
with the jar file name is in your JAR_TEMP_PATH or /tmp directory.

A possible cause of this error is using "thisjar" instead of using
the jar ID in the deploy.txt file.


-47086	Cannot specify current as default value with non-datetime column type.

You cannot assign a default value consisting of the current time from
the system clock when the column data type is not DATETIME. Modify the
column data type to DATETIME (if permitted), or specify a different
default value for the column.


-47087	Cannot specify null default value when column doesn't accept nulls.

Select a different (nonnull) default value for the column, or modify
the column to accept null values.


-47088	Cannot specify server or site as a default value with this column type.

You cannot specify a default value that consists of the current
database server name or current site name for a column that is not a
CHAR, NCHAR, VARCHAR, or NVARCHAR data type. Change the column data
type (if permitted), or specify a different default value.


-47089	Cannot specify server or site as a default value with this column
length.

For specifying a default value that consists of the current database server
name or current site name for a CHAR, NCHAR, VARCHAR, or NVARCHAR column,
the minimum column length must be 18. Increase the column length, or specify
a different default value.


-47090	Cannot specify today as a default value with this column type.

You cannot specify a default value that consists of the current system
date for a column when the column data type is not DATE. Change the
column data type (if permitted), or specify a different default value.


-47091	Cannot specify user as a default value with this column type.

You cannot specify a default value that consists of the login name of
the current user for a column that is not a CHAR, NCHAR, VARCHAR, or
NVARCHAR data type. Change the column data type (if permitted), or
specify a different default value.


-47092	Cannot specify user as a default value with this column length.

In order to specify a default value that consists of the login name of
the current user for a CHAR, NCHAR, VARCHAR, or NVARCHAR column, the
minimum column length must be 8. Increase the column length, or specify
a different default value.


-47093	Cannot create unique or primary key constraint with column type of
BYTE or TEXT.

In order to create a primary or unique constraint on the column, change
the column data type from TEXT or BYTE, if permitted.


-47095	Column not found in referenced table.

You cannot create a foreign-key constraint on a column that does not
exist in the referenced table. Specify a different referencing or
referenced column for the constraint.


-47098	Number of columns in composite list exceeds maximum.

You cannot include more than 16 column names in a single primary or
unique constraint on GBase Database Server. Reduce the number
of columns that are specified in the constraint definition.


-47099	You cannot modify an existing constraint.

You cannot use the ALTER TABLE menu options to modify an existing
constraint. An asterisk preceding the name identifies existing
constraints.


-47100	Column not in this table.

You cannot create a constraint on a column that does not exist in the
table.


-47101	To drop an existing constraint, the current field must be constraint
name.

The cursor is in the wrong field on the screen. Move the cursor to
highlight an entry in the Constraint Name field to drop all columns
that are associated with a primary key, check, or unique constraint.
Highlight the Constraint field to drop all columns that are associated
with a foreign-key constraint.


-47102	You have exceeded the temporary buffer size.

The buffer in the SQL editor, which holds the check-constraint value
and the literal default-constraint value, is full. If you are modifying
the check constraint, a different editor might have a larger buffer.


-47104	The fill factor percentage must be a positive integer not exceeding 100.

You tried to specify a fill factor percentage that is a negative number
or is greater than 100 percent. Specify a different percentage, or
press RETURN to accept the default value of 90 percent.


-47105	Dbspace has already been selected as part of the strategy.

You cannot use the same dbspace more than once in a fragmentation
strategy. The dbspace you selected is already part of the strategy.
Select another dbspace from the current list.


-47106	Table has already been selected as part of the attaching strategy.

A table can be attached to a fragmentation strategy only once. The
table that you selected is already part of the fragmentation strategy.
Select another table from the current list.


-47107	An Alter Table option has already been altered.

You can execute only one menu option in an ALTER FRAGMENT menu session,
and it cannot be applied to the current strategy more than once. For
example, you can add only one dbspace to a round-robin strategy, and
you cannot delete a dbspace during the same ALTER TABLE session.


-47108	Dbspace is not part of the current strategy.

You have specified a dbspace that is not part of the current
fragmentation strategy. Select a dbspace from the list of available
dbspaces that is displayed on the screen.


-47109	A Dbspace is required if a position is defined.

You have specified a dbspace as part of your attaching table strategy.
This action indicates that you wish to position the attaching table
before or after a dbspace that is part of the fragmentation strategy
being attached to. You must define a before or after position.

-47111	ALTER currently not supported for this strategy.

The fragmentation strategy you have chosen does not currently support
ALTER FRAGMENT operations. 

If that fragmentation strategy is a design requirement of your application,
consider creating a new table that uses the strategy, and then loading
existing data into the new table whose fragmentation strategy matches 
your requirements.


-47112	Must choose a fragmentation key.

You have not chosen a fragmentation key for the current fragmentation
strategy. 

You must mark one of the table columns as selected, from the Fragment-key
menu of the dbaccess utility.

-47113	Must write an interval value expression.

You have not written an interval value expression for the fragmentation 
strategy. Interval fragmentation requires that you choose an interval
value appropriate for the fragmentation key.

For example, with a fragment key of type DATE, valid interval values are:
   
          INTERVAL(YYYY-MM) YEAR TO MONTH
          INTERVAL(MM) MONTH TO MONTH
          INTERVAL(YYYY) YEAR TO YEAR
          INTERVAL(DD) DAY TO DAY

Where:
          YYYY = years
          MM = months
          DD = days

For fragment keys of INTEGER, and other numeric types, the interval value 
must be a constant expression that evaluates to the same type as the fragment key.

For example, with a fragment key of INTEGER TYPE, some valid interval values are:

        1337
        0.1337E4

-47114	Must specify at least one partition.

You must have at least one partition for data storage using the selected 
fragmentation strategy.

Within a given dbspace, each partition name that you declare must be unique
among the names of partitions of the same table or index.


-47115	Missing name from a partition.

You have omitted the name of one of the partitions in the fragmentation strategy. 

Within a given dbspace, each partition name that you declare must be unique
among the names of partitions of the same table or index.

-47116	Missing expression from a partition.

You have omitted the partition expression from one of the partitions in the
fragmentation strategy.

1. For Interval fragmentation, valid partition expressions are of the form:

   a. VALUES < {transition}

   The transition value, depends on the interval value, and the data type of 
   the fragment key.

   For example, with fragment key of DATE type, and an interval value of 
   INTERVAL(1-3) YEAR TO MONTH, some valid partition expressions are:

       VALUES < DATETIME(1980-02-13) YEAR TO DAY 
       VALUES < DATETIME(1986-11-01) YEAR TO DAY

   b. VALUES IS NULL

   This form is used to specify the NULL partition. You can only have
   one NULL partition in a fragmentation strategy.

2. For List fragmentation, valid partition expressions are of the form:

   a. VALUES ({constant, ...})

   The constant value must be valid for the data type of the fragment key.

   For example, with a fragment key of CHAR(20) type, some valid partition
   expressions are:

       VALUES ("FL", "CA", "NY", "NJ")
       VALUES ("OH", "MD", "MI")
       VALUES (NULL)

   b. REMAINDER

   This form is used to specify the REMAINDER partition. There can only
   be one REMAINDER partition, and it must be the last partition in
   the partition list.

-47117	Missing dbspace name from a partition.

You have omitted the dbspace name from one of the partitions in the 
fragmentation strategy.


-55900	Missing option list.

You specified a processor command-line option of -cc, -l, or -r and
terminated the option incorrectly. For information on how command-line
graphics terminate these options, see "Command-Line Conventions" on
page 6 of the Introduction or online help.


-55901	Unrecognized sub-option (option_name).

Text (option_name) following a colon (:) was not a valid suboption. For
information on which suboptions are valid with the option, see the
online help.


-55902	Incompatible file extension (file_extension) in option list.

For ESQL/C: You specified a processor command-line option of -cc -l,
or -r and terminated the option with an incorrect file extension. For
information on how command-line graphics terminate these options, see
the GBase ESQL/C Programmer's Supplement for Microsoft Windows
Environments or online help.

For ESQL/COBOL: You specified a processor command-line option of -cb,
-l, or -r and terminated the option with an incorrect file extension.
For information on how command-line graphics terminate these options,
see the GBase ESQL/COBOL Programmer's Supplement for Microsoft
Windows Environments or online help.


-55903	Processor type option conflict.

You specified more than one CPU-type command-line option (for example,
-cpu:i386 and -pm) that identifies the CPU type. Enter the esql command
again without the conflicting options.


-55904	Error in spawned program.

For ESQL/C: The esql command processor created a program (compiler,
linker, or resource compiler) that returned an error and caused esql to
terminate. Check the generated output to determine the error, resolve
the error, and run the esql command processor again.

For ESQL/COBOL: The esqlcobo command processor created a program
(compiler, linker, or resource compiler) that returned an error and
caused esqlcobo to terminate. Check the generated output to determine
the error, resolve the error, and run the esqlcobo command processor
again.


-55905	Illegal character in filename, `file_name'.

The file file_name contains one or more invalid characters.


-55906	Run-time option conflict.

For ESQL/C: You specified more than one runtime option (for example,
-runtime:libc and -rt:d). Enter the esql command again without the
conflicting options.

For ESQL/COBOL: You specified more than one runtime option (for
example, -runtime:mfrts32 and -rt:s). Enter the esqlcobo command again
without the conflicting options.


-55907	Subsystem option conflict.

For ESQL/C: You specified more than one subsystem option (for example,
-ss:c and -S:w). Enter the esql command again without the conflicting
options.

For ESQL/COBOL: You specified more than one subsystem option (for
example, -ss:c and -Sw). Enter the esqlcobo command again without the
conflicting options.


-55909	I/O error in file (file_name).

An I/O error occurred in the file file_name. Probably no space is
available. Increase available space and enter the command again.


-55910	Option (option_name) is no longer supported.

For ESQL/C: This release does not support the specified command-line
option. For valid options, check the GBase ESQL/C Programmer's
Supplement for Microsoft Windows Environments.

For ESQL/COBOL: This release does not support the specified
command-line option. For valid options, check the GBase ESQL/COBOL
Programmer's Supplement for Microsoft Windows Environments.


-55911	Unable to open the log file.

For ESQL/C: Unable to open the ESQL/C preprocessor log file. Probably
no space is available. Increase available space and enter the command
again.

For ESQL/COBOL: Unable to open the ESQL/COBOL preprocessor log file.
Probably no space is available. Increase available space and enter the
command again.


-55912	Incompatible file extension for -e option.

For ESQL/C: You specified the -e command-line option but did not
specify an ESQL/C source file (.ec file extension). Enter the command
again, specifying the name of the .ec file.

For ESQL/COBOL: You specified the -e command-line option but did not
specify an ESQL/COBOL source file (.eco file extension). Enter the
command again, specifying the name of the .eco file.


-55913	Missing target filename for `-o' option.

You specified the -o command-line option but did not specify an output
file. Enter the command again, specifying the filename of the output
file.


-55914	Incompatible file extension for `-c' option.

You specified the -c command-line option but specified an incompatible
file extension. Enter the command again, specifying the name of the
file with a compatible file extension.


-55915	Missing name of log file for `-log' option.

You specified the -log command-line option but omitted a filename for
the log file. Enter the command again, specifying the name of the log
file.


-55917	Compiler option conflict.

You specified more than one command-line option that identifies the
compiler type (for example, -mc and -bc). Enter the esql command again
without the conflicting option.


-55918	Missing name of source file list file for '-f' option.

For ESQL/C: You specified the -f command-line option but omitted a
filename for the list of ESQL/C source files. Enter the command again,
specifying the name of the list file.

For ESQL/COBOL: You specified the -f command-line option but omitted a
filename for the list of ESQL/COBOL source files. Enter the esqlcobo
command again, specifying the name of the list file.


-55919	Application type option conflict.

For ESQL/C: You specified more than one application type option (for
example, -wd and -target:exe). Enter the esql command again without the
conflicting options.

For ESQL/COBOL: You specified more than one application type option
(for example, -wd and -target:exe). Enter the esqlcobo command again
without the conflicting options.


-55920	Cannot open ESQL response file 'file_name'.

For ESQL/C: The esql command processor cannot open the specified ESQL
command-line response file. Verify that the name of the file after the
'@' option exists.

For ESQL/COBOL: The esqlcobo command processor cannot open the
specified ESQLCOBO command-line response file. Verify that the name of
the file after the `@' option exists.


-55921	Unable to allocate memory.

For ESQL/C: ESQL cannot allocate memory. Terminate one or more
applications and enter the command again.

For ESQL/COBOL: ESQL/COBOL cannot allocate memory. Terminate one or
more applications and enter the command again.


-55922	Preprocessor detected errors.

For ESQL/C: The ESQL/C preprocessor detected one or more errors,
causing the processor to stop. Correct the errors and rerun.

For ESQL/COBOL: The ESQL/COBOL preprocessor detected one or more
errors, causing the esqlcobo command processor to stop. Correct the
errors and rerun.


-55923	No source or object file.

You did not specify the name of a source or the program object file. No
files could be passed to the linker. Enter the command again,
specifying the name of the file to pass to the linker.


-55925	Cannot open file 'file_name'.

For ESQL/C: The esql command processor could not open the specified
file. If you specified the -f option, check that the source list file
exists. If -f was omitted, this error indicates that the processor
cannot find the necessary space to create some file.

For ESQL/COBOL: The esqlcobo command processor could not open the
specified file. If you specified the -f option, check that the source
list file exists. If -f was omitted, this error indicates that the
processor cannot find the necessary space to create some file.


-55926	Unable to spawn the compiler.

For ESQL/C: The esql command processor cannot start the compiler. Make
sure the directory that contains your C compiler is included in the
PATH environment variable.

For ESQL/COBOL: The esqlcobo command processor cannot start the
compiler. Make sure the directory that contains your COBOL compiler is
included in the PATH environment variable.


-55927	Unable to spawn the linker.

For ESQL/C: The esql command processor cannot start the linker. Make
sure the directory that contains your linker is included in the PATH
environment variable.

For ESQL/COBOL: The esqlcobo command processor cannot start the
linker. Make sure that the directory containing your linker is
included in the PATH environment variable.


-55928	Unable to spawn the resource compiler.

For ESQL/C: The esql command processor cannot start the resource
compiler. Make sure the directory that contains your resource compiler
is included in the PATH environment variable.

For ESQL/COBOL: The esqlcobo command processor cannot start the
resource compiler. Make sure the directory that contains your resource
compiler is included in the PATH environment variable.


-55929	Missing ESQL response file.

For ESQL/C: The esql command processor cannot locate the ESQL
command-line response file (specified after the '@'). Verify that the
filename is correct.

For ESQL/COBOL: The esqlcobo command processor cannot locate the
ESQLCOBO command-line response file (specified after the `@'). Verify
that the filename is correct.


-55930	Too many parameters specified.

You exceeded the maximum number of parameters for the preprocessor,
compiler, linker (linker options or list of libraries), or resource
compiler. Reduce the number of parameters and run the command again.


-55931	No source file provided.

For ESQL/C: You have not provided the name of an ESQL/C source file.
Possibly you specified the -e option (preprocess only) and omitted the
name of an ESQL/C source file (.ec file extension). Possibly you
specified the -c option (preprocess and compile only) and omitted the
name of an ESQL/C source file (.ec) or C source file (.c).

For ESQL/COBOL: You have not provided the name of an ESQL/COBOL source
file. Possibly you specified the -e option (preprocess only) and
omitted the name of an ESQL/COBOL source file (.eco file extension).
Possibly you specified the -c option (preprocess and compile only) and
omitted the name of an ESQL/COBOL source file (.eco) or COBOL source
file (.cbl).


-55932	File_name has incompatible file format.

The command-line response file file_name is too large. Decrease its
size and run the command again.


-55933	esql: error error_num: Multibyte filter detected error.

The ESQL/C multibyte filter has encountered one of the following
conditions:

   -Unable to initialize GLS library routines

   -Unable to write to the source (.c) file when it renames the file

   -Unable to write to the output file

   -Some other I/O error

Check the file permissions on the directory that contains your ESQL/C
source file to ensure that you have read and write permission. Also
check the file permissions on the ESQL/C source file itself to ensure
that you have read and write permission.


-73002	Cannot issue a SQL statement in the secondary thread.

You need to define the user-defined routine as variant.


-73003	An invalid argument is specified.

Either the return type buffer is empty or the length of the buffer is
not valid.


-73018	Unsupported data type.

Check the file permissions on the directory that contains your ESQL/C
source file to ensure you have read and write permission. Also check
the file permissions on the ESQL/C source file itself to ensure you
have read and write permission.


-79700	Method not supported.

GBase JDBC Driver does not support this JDBC method.


-79701	Blob not found

The GBase JDBC Driver encountered an exception during the
access of BLOB. With JDK 1.4 and above, the initial exception is available 
through the cause facility of java.lang.Throwable.


-79702	Can't create new object 

The software could not allocate memory for a new String object. 


-79703	Row/column index out of range

The row or column index is out of range. Compare the index to the 
number of rows and columns expected from the query to ensure that it 
is within range.


-79704	Can't load driver

GBase JDBC Driver could not create an instance of itself and 
register it in the DriverManager class. The rest of the SQLException 
text describes what failed.


-79705	Incorrect URL format

The database URL you have submitted is invalid. GBase JDBC Driver 
does not recognize the syntax. Check the syntax and try again.


-79706	Incomplete input

An invalid character was found during conversion of a String value to 
an IntervalDF or IntervalYM object. For correct values, see the GBase JDBC
Driver Programmer's Guide.


-79707	Invalid qualifier.

An error was found during construction of an INTERVAL qualifier from 
atomic elements: length, start, or end values. Check the length, start, 
and end values to verify that they are correct.

For correct values, see the GBase JDBC Driver Programmer's Guide.


-79708	Can't take null input.

The string you provided is null. GBase JDBC Driver does not 
understand null input in this case.

Check the input string to ensure that it has the proper value.


-79709	Error in date format.

The expected input is a valid date string in the following format: 
yyyy-mm-dd.

Check the date and verify that it has a four-digit year followed
by a valid two-digit month and two-digit day. The delimiter 
must be a hyphen (-).


-79710	Syntax error in SQL escape clause.

Invalid syntax was passed to a JDBC escape clause. Curly braces and
a keyword demarcate valid JDBC escape clause syntax: for example,
{keyword syntax}.

Check the JDBC 2.0 documentation from Sun Microsystems for a list
of valid escape clause keywords and syntax.


-79711	Error in time format.

An invalid time format was passed to a JDBC escape clause.

The escape clause syntax for time literals has the following format:

   {t "hh:mm:ss"}.


-79712	Error in timestamp format.

An invalid timestamp format was passed to a JDBC escape clause.

The escape clause syntax for timestamp literals has the following format:

   {ts "yyyy-mm-dd hh:mm:ss.f..."}.


-79713	Incorrect number of arguments.

An incorrect number of arguments was passed to the scalar function 
escape syntax. The correct syntax is {fn function(arguments)}.

Verify that the correct number of arguments was passed to the function.


-79714	Type not supported.

You specified a data type that GBase JDBC Driver does not support.

Check your program to make sure the data type is among those that the
driver supports.


-79715	Syntax error.

Invalid syntax was passed to a JDBC escape clause. Curly braces and
a keyword demarcate valid JDBC escape clause syntax: for example,
{keyword syntax}.

Check the JDBC 2.0 documentation from Sun Microsystems for a list
of valid escape clause keywords and syntax.


-79716	System or internal error.

An operating or runtime system error or a driver internal error 
occurred.

The accompanying message describes the problem.


-79717	Invalid qualifier length.

The length value for an INTERVAL object is incorrect.

For correct values, see the GBase JDBC Driver Programmer's Guide.


-79718	Invalid qualifier start code.

The start value for an INTERVAL object is incorrect.

For correct values, see the GBase JDBC Driver Programmer's Guide.


-79719	Invalid qualifier end code.

The end value for an INTERVAL object is incorrect.

For correct values, see the GBase JDBC Driver Programmer's Guide.


-79720	Invalid qualifier start or end code.

The start or end value for an INTERVAL object is incorrect.

For correct values, see the GBase JDBC Driver Programmer's Guide.


-79721	Invalid interval string.

An error occurred during conversion of a STRING value to an IntervalDF or
IntervalYM object.

For the correct format, see the GBase JDBC Driver Programmer's Guide.


-79722	Numeric characters expected.

An error occurred during conversion of a STRING value to an IntervalDF or 
IntervalYM object. A numeric value was expected and not found.

For the correct format, see the GBase JDBC Driver Programmer's Guide.


-79723	Delimiter characters expected.

An error occurred during conversion of a STRING value to an IntervalDF or 
IntervalYM object. A delimiter was expected and not found.

For the correct format, see the GBase JDBC Driver Programmer's Guide.


-79724	Characters expected.

An error occurred during conversion of a STRING value to an IntervalDF or 
IntervalYM object. End of string was encountered before conversion was
complete.

For the correct format, see the GBase JDBC Driver Programmer's Guide.


-79725	Extra characters found.

An error occurred during conversion of a STRING value to an IntervalDF or 
IntervalYM object. End of string was expected, but more characters were
in the string.

For the correct format, see the GBase JDBC Driver Programmer's Guide.


-79726	Null SQL statement.

The SQL statement passed in was null.

Check the SQL statement string in your program to make sure the string
contains a valid statement.


-79727	Statement was not prepared.

The SQL statement contains one or more host variables but was not
prepared properly.

If you use host variables in your SQL statement (for example, insert
into mytab values (?, ?);), you must use connection.prepareStatement()
to prepare the SQL statement before you can execute it.


-79728	Unknown object type.

If this object type is a null opaque type, the type is unknown and
cannot be processed. If this object type is a complex type, the data
in the collection or array is of an unknown type and cannot be mapped
to an GBase type. If this object type is a row, one of the elements
in the row cannot be mapped to an GBase type.

Verify the customized type mapping or data type of the object.


-79729	Method cannot take argument.

An argument was supplied for a method that does not take an
argument.

Refer to your Java API specification or the GBase JDBC Driver
Programmer's Guide to make sure you are using the method properly.


-79730	Connection not established.

A method was not called to establish a connection.

You must obtain the connection by calling the
DriverManager.getConnection() method first.


-79731	MaxRows out of range.

The specified MaxRows value is invalid because it is not within
the range of possible values for MaxRows.

Make sure you specify a value between 0 and Integer.MAX_VALUE.


-79732	Illegal cursor name.

The specified name is not a valid cursor name.

Make sure the string passed in is not null or empty.


-79733	No active result.

The statement does not contain an active result.

Check your program logic to make sure you have called the executeXXX()
method before you refer to the result.


-79734	GBASEDBTSERVER has to be specified.

GBASEDBTSERVER is a property required for connecting to an 
GBase database.

You can specify the GBASEDBTSERVER property in the database URL 
or as part of a properties object that is passed to the 
connect() method.


-79735	Can't instantiate protocol.

An internal error occurred during a connection attempt.

Call GBase Technical Support.


-79736	No connection/statement establish yet.

There is no current connection or statement.

Check your program to make sure a connection was properly established
or a statement was created.


-79737	No meta data.

The SQL statement attempts to use metadata, but no metadata is
available.

Make sure the statement generates a result set before you attempt
to use it.


-79738	No such column name.

The specified column does not exist.

Make sure the column name is correct.


-79739	No current row.

The cursor is not properly positioned.

You must first position the cursor within the result set by using
a method such as resultset.next(), resultset.beforefirst(),
resultset.first(), or resultset.absolute().


-79740	No statement created.

There is no current statement.

Make sure the statement was properly created.


-79741	Can't convert to.

No data conversion is possible from the column data type to the 
one specified. The actual data type is appended to the end of this 
message.

Review your program logic to make sure that the conversion 
you asked for is supported. For the data mapping matrix,
see the GBase JDBC Driver Programmer's Guide.


-79742	Can't convert from.

No data conversion is possible from the data type you specified to 
the column data type. The actual data type is appended to the end 
of this message.

Check your program logic to make sure that the conversion you asked
for is supported. For the data mapping matrix, see the GBase
JDBC Driver Programmer's Guide.


-79743	Forward fetch only.

This error number is not in use by a current version of GBase JDBC
Driver.


-79744	Transactions not supported.

The user tried to call commit() or rollback() on a database that 
does not support transactions or has tried to set autoCommit to false 
on a nonlogging database.

Verify that the current database has the correct logging mode and
review the program logic.


-79745	Read only mode not supported.

GBase does not support read-only mode.


-79746	No Transaction Isolation on non-logging db's.

GBase does not support setting the transaction isolation level on 
nonlogging databases.


-79747	Invalid transaction isolation level.

If the database server could not complete the rollback, this error occurs. 

For details about why the rollback failed, see the rest of the
SQLException message. This error also occurs if an invalid transaction
level is passed to setTransactionIsolation(). The valid values follow:

   TRANSACTION_READ_UNCOMMITTED
   TRANSACTION_READ_COMMITTED
   TRANSACTION_REPEATABLE_READ
   TRANSACTION_SERIALIZABLE


-79748	Can't lock the connection.

GBase JDBC Driver normally locks the connection object just before 
beginning the data exchange with the database server. The driver 
could not obtain the lock.

Only one thread at a time should use the connection object.


-79749	Number of input values does not match number of question marks.

The number of variables that you set using the PreparedStatement.setXXX()
methods in this statement does not match the number of ? placeholders
that you wrote into the statement.

Locate the text of the statement and verify the number of placeholders,
and then check the calls to PreparedStatement.setXXX().


-79750	Method only for queries.

The Statement.executeQuery(String) and PreparedStatement.executeQuery() 
methods should only be used if the statement is a SELECT statement.

For other statements, use the Statement.execute(String), 
Statement.executeBatch(), Statement.executeUpdate(String), 
Statement.getUpdateCount(), Statement.getResultSet(), 
or PreparedStatement.executeUpdate() method.


-79751	Forward fetch only.

The result set is not set to FETCH_FORWARD.

Call Resultset.setFetchDirection(ResultSet.FETCH_FORWARD) to reset 
the direction.


-79752	Insufficient Blob data

The GBase JDBC Driver encountered an unexpected exception during
the access of BLOB. The GBase JDBC Driver was unable to transfer
the specified number of bytes.


-79753	Out of Blob memory

The GBase JDBC Driver was unable to allocate memory while handling
BLOB data.This error occurs when the JDBC driver fails to allocate memory 
required to perform LOB operations in memory.
You can reset the LOBCACHE or IFX_CODESETLOB properties to perform blob 
operations using temporary files rather than using memory.


-79754	Write Fault

The GBase JDBC Driver encountered an error writing to a temporary
file while handling LOB data.


-79755	Object is null.

The object passed in is null.

Check your program logic to make sure your object reference is valid.


-79756	must start with 'jdbc'.

The first token of the database URL is not the keyword jdbc (case 
insensitive).

Start the URL with jdbc, as in the following example:

   jdbc:gbasedbt-sqli://mymachine:1234/mydatabase:user=me:password=secret


-79757	Invalid sub-protocol.

The current valid subprotocol that GBase supports is gbasedbt-sqli.

Use the gbasedbt-sqli subprotocol.


-79758	Invalid ip address.

The format of the database server's IP address is invalid.

When you connect to an GBase server with an IP address,
specify the IP address in the correct format. A valid IP address is
a set of four numbers between 0 and 255, separated by dots (.): for
example, 127.0.0.1.


-79759	Invalid port number.

The port number must be a valid four-digit number, as follows:

   jdbc:gbasedbt-sqli://mymachine:1234/mydatabase:user=me:password=secret

In this example, 1234 is the port number.


-79760	Invalid database name.

This statement contains the name of a database in some invalid format. 

The maximum length for database names and cursor names depends on the 
version of the database server. In 7.x, 8.x, and 9.1x versions of the 
GBase server, the maximum length is 18 characters. For 
GBase SE, database names should be no longer than 10 characters 
(fewer in some host operating systems). Both database and cursor names 
must begin with a letter and contain only letters, numbers, and underscore 
characters. In 6.0 and later versions of the database server, database
and cursor names can begin with an underscore. In MS-DOS systems, 
filenames can be a maximum of eight characters plus a three-character 
extension.


-79761	Invalid Property format.

The database URL accepts property values in key=value pairs. For example, 
user=gbasedbt:password=gbasedbt adds the key=value pairs to the list of 
properties that are passed to the connection object.

Check the syntax of the key=value pair for syntax errors. Make sure
there is only one = sign; that no spaces separate the key, value,
or =; and that key=value pairs are separated by one colon (:), again
with no spaces.


-79762	Attempt to connect to a non 5.x server.

When connecting to a Version 5.x database server, the user must set the 
database URL property USE5SERVER to any non-null value. If a connection 
is then made to a Version 6.0 or later database server, this exception 
is thrown.

Verify that the version of the database server is correct and 
modify the database URL as needed.


-79763	Only CONCUR_READ_ONLY is supported.

GBase JDBC Driver supports only the ResultSet.CONCUR_READ_ONLY method. 

You can only call the Connection.createStatement(int, int), 
Connection.prepareStatement(String, int, int), or 
Connection.CallableStatement(String, int, int) method with a result 
set concurrency value of CONCUR_READ_ONLY.


-79764	Invalid Fetch Direction value.

An invalid fetch direction was passed as an argument to the 
Statement.setFetchDirection() or ResultSet.setFetchDirection() method.

Verify that the argument is one of the valid fetch direction values,
which are FETCH_FORWARD, FETCH_REVERSE, and FETCH_UNKNOWN.


-79765	ResultSet Type is TYPE_FETCH_FORWARD, direction can only be 
FETCH_FORWARD.

The result set type has been set to TYPE_FORWARD_ONLY, but the 
setFetchDirection() method has been called with a value other than 
FETCH_FORWARD.

Verify that the direction specified is consistent with the result type
specified.


-79766	Incorrect Fetch Size value.

The Statement.setFetchSize() method has been called with an invalid 
value.

Verify that the value passed in is greater than 0. If the setMaxRows
method has been called, the fetch size must not exceed that value.


-79767	ResultSet Type is TYPE_FORWARD_ONLY.

A method such as ResultSet.beforeFirst(), ResultSet.afterLast(), 
ResultSet.first(), ResultSet.last(), ResultSet.absolute(), 
ResultSet.relative(), ResultSet.current(), or ResultSet.previous() 
has been called, but the result set type is TYPE_FORWARD_ONLY. 

Call only the ResultSet.next() method if the result set type is 
TYPE_FORWARD_ONLY.


-79768	Incorrect row value.

The ResultSet.absolute(int) method has been called with a value of 0. 
The parameter must be greater than 0.


-79769	A customized type map is required for this data type.

No customized type map exists for an opaque type.

You must register a customized type map to use any opaque types.


-79770	Cannot find the SQLTypeName specified in the SQLData or Struct.

The SQLTypename object you specified in the SQLData or Struct class 
does not exist in the database.

Make sure that the type name is valid.


-79771	Input value is not valid.

The input value is not accepted for this data type.

Make sure this input is valid for this data type.


-79772	No more data to read. Verify your SQLdata class or
getSQLTypeName().

You have asked for more data than is available.

Check your SQLData class to make sure it matches what is in the
database schema. The SQLTypeName object might also be incorrect.


-79773	Invalid arguments.

The GBase JDBC Driver throws this exception whenever a method is called
with an invalid argument. Refer to the JDBC API documentation to ensure that a 
valid argument is being passed to the method that is throwing this exception.


-79774	Unable to create local file.

Large object data read from the database server can be stored either in 
memory or in a local file. If the LOBCACHE value is 0 or the large object 
size is greater than the LOBCACHE value, the large object data from the 
database server is always stored in a file. In this case, if a security 
exception occurs, GBase JDBC Driver makes no attempt to store the 
large object into memory and throws this exception.


-79775	Only TYPE_SCROLL_INSENSITIVE and TYPE_FORWARD_ONLY are
supported.

GBase JDBC Driver only supports a result set type of 
TYPE_SCROLL_INSENSITIVE and TYPE_FORWARD_ONLY.

Use only these values.


-79776	Type requested (<row-data>) does not match row type
information (<row-element>) type.

Row-type information was acquired either through the system catalogs or 
through the supplied row definition. The row data provided does not match 
this row element type.

The type information must be modified, or the data must be provided.


-79777	readObject/writeObject() only supports UDTs, Distincts and
complex types.

The SQLData.writeObject() method was called for an object that is not a 
user-defined, distinct, or complex type.

Verify that you have provided customized type-mapping information.


-79778	Type mapping class must be a java.util.Collection implementation.

You provided a type mapping to override the default for a set, list, or 
multiset data type, but the class does not implement the
java.util.Collection interface.


-79779	To insert null data into a row use java null representation.

The GBase JDBC Driver throws this exception whenever an application
attempts to set row column null in an incorrect manner. Ensure that java null
representation is used instead of an object to set null data.


-79780	Data within a collection must all be the same Java class and
length.

Collection data was of different Java classes or lengths.

Verify that all the objects in the collection are of the same class. 


-79781	Index/Count out of range.

Array.getArray() or Array.getResultSet() was called with index and count
values. Either the index is out of range or the count is too big.

Verify that the number of elements in the array is sufficient for the
index and count values.


-79782	Method can be called only once.

Make sure methods like Statement.getUpdateCount() and
Statement.getResultSet() are called only once per result. 


-79783	Encoding or code set not supported.

The encoding or code set entered in the DB_LOCALE or CLIENT_LOCALE 
variable is not valid.

For valid code sets, see the GBase JDBC Driver Programmer's Guide.


-79784	Locale not supported.

The locale entered in the DB_LOCALE or CLIENT_LOCALE variable is not 
valid.

For valid locales, see the GBase JDBC Driver Programmer's Guide.


-79785	Unable to convert JDBC escape format date string to localized 
date string.

The JDBC escape format for date values must be specified in the format 
{d 'yyyy-mm-dd'}.

Verify that the JDBC escape date format specified is correct. Verify
the DBDATE and GL_DATE settings for the correct date string format if
either of these was set to a value in the connection database URL
string or property list.


-79786	Unable to build a Date object based on localized date string
representation.

The localized date string representation specified in a CHAR, VARCHAR, or 
LVARCHAR column is not correct, and a date object cannot be built based on 
the year, month, and day values.

Verify that the date string representation conforms to the DBDATE or
GL_DATE date formats if either one of these is specified in a connection
database URL string or property list. If neither DBDATE or GL_DATE is
specified but a CLIENT_LOCALE or DB_LOCALE is explicitly set in a
connection database URL string or property list, verify that the 
date string representation conforms to the JDK short default format
(DateFormat.SHORT).


-79787	Blob/Clob object has not been created from a BLOB/CLOB column.

The GBase JDBC Driver throws this exception whenever an application
calls methods that attempt to use smart large object functionality on an
object that does not represent a smart large object.
For example, smart large objects (BLOB or CLOB) support random access to the
data while simple large objects (TEXT or BYTE) do not support random access.
Ensure that the object being passed has been created from a smart large object.


-79788	User name must be specified.

The user name is required to establish a connection with GBase JDBC
Driver.

Make sure you pass in user=your_user_name as part of the database URL
or one of the properties.


-79789	Server does not support GLS variables DB_LOCALE, CLIENT_LOCALE,
or GL_DATE.

These variables can only be used if the database server supports GLS.

Check the documentation for your database server version and omit these
variables if they are not supported.


-79790	Invalid complex type definition string.

The value that the getSQLtypeName() method returns is either null or invalid.

Check the string to verify that it is either a valid named-row name or a
valid row-type definition.


-79791	Invalid object. Cannot be inserted into clob/blob column.

GBase JDBC Driver throws this exception whenever an application supplies
an invalid object to the JDBC driver for insertion in a clob/blob column.
The JDBC driver attempts to determine if the provided java object may be used 
for inserting data in a blob/clob column. If the JDBC driver discovers that the 
object is not compatible, this exception is thrown. Make sure that the 
application is passing the correct type of object when calling 
PreparedStatement::setObject() method.


-79792	Row must contain data.

The Array.getAttributes() or Array.getAttributes(Map) method has
returned 0 elements.

Make sure the method returns a nonzero number.


-79793	Data in array does not match getBaseType() value.

The Array.getArray() or Array.getArray(Map) method returns an array where 
the element type does not match the JDBC base type.


-79794	Row length provided (<row-data>) doesn't match row type
information (<length>).

Data in the row does not match the length in the row-type information. 

You do not have to pad string lengths to match what is in the row 
definition, but lengths for other data types should match.


-79795	Row extended id provided (<row-data>) doesn't match row type 
information (<extended-id>). 

The extended ID of the object in the row does not match the extended 
ID as defined in row-type information.

Either update the row-type information (if you are providing the row
definition) or check the type-mapping information.


-79796	Cannot find UDT, distinct or named row (<type-name>) in database.

The getSQLTypeName() method has returned a name that cannot be 
found in the database.

Verify that the Struct or SQLData object returns the correct
information.


-79797	DBDATE setting must be at least 4 characters and no longer 
than 6 characters.

This error occurs because the DBDATE format string that is passed to 
the database server either has too few characters or too many.

To fix the problem, verify the DBDATE format string with the user 
documentation and make sure that the correct year, month, day, and 
possibly era parts of the DBDATE format string are correctly identified.


-79798	A numerical year expansion is required after 'Y' character in
DBDATE string.

This error occurs because the DBDATE format string has a year designation 
(specified by the character Y), but no character follows the year
designation to denote the numerical year expansion (2 or 4).

To fix the problem, modify the DBDATE format string to include the
numerical year expansion value after the Y character.


-79799	An invalid character is found in the DBDATE string after
the 'Y' character.

This error occurs because the DBDATE format string has a year 
designation (specified by the character Y), but the character that
follows the year designation is not a 2 or 4 (for two-digit years and
four-digit years, respectively).

To fix the problem, modify the DBDATE format string to include the
required numerical year expansion value after the Y character. Only
a 2 or 4 character should immediately follow the Y character designation.


-79800	No 'Y' character is specified before the numerical year
expansion value.

This error occurs because the DBDATE format string has a numerical year 
expansion (2 or 4 to denote two-digit years or four-digit years, 
respectively), but the year designation character (Y) was not found 
immediately before the numerical year expansion character specified.
 
To fix the problem, modify the DBDATE format string to include the 
required Y character immediately before the numerical year expansion 
value requested.


-79801	An invalid character is found in DBDATE format string.

This error occurs because the DBDATE format string has a character that 
is not allowed.

To fix the problem, modify the DBDATE format string to include only
the correct date part designations: year (Y), numerical year expansion
value (2 or 4), month (M), and day (D). Optionally, you can include an
era designation (E) and a default separator character (hyphen, dot, or
slash), which is specified at the end of the DBDATE format string.
For further information on correct DBDATE format string character
designations, refer to the user documentation.


-79802	Not enough tokens are specified in the string representation 
of a date value.

This error occurs because the date string specified does not have the 
minimum number of tokens or separators needed to form a valid date value 
(composed of year, month, and day numerical parts). For example, 
12/15/98 is a valid date string representation with the slash character 
as the separator or token. But 12/1598 is not a valid date string 
representation because there are not enough separators or tokens.
To fix the problem, modify the date string representation to include
a valid format for separating the day, month, and year parts of a date 
value.


-79803	Date string index out of bounds during date format parsing to 
build Date object.

This error occurs because there is not a one-to-one correspondence
between the date string format required by DBDATE or GL_DATE and the
actual date string representation you defined. For example, if GL_DATE
is set to %b %D %y and you specify a character string of 'Oct', there is
a definite mismatch between the format required by GL_DATE and the
actual date string. 

To fix the problem, modify the date string representation of the DBDATE 
or GL_DATE setting so that the date format specified matches one-to-one 
with the required date string representation.


-79804	No more tokens are found in DBDATE string representation of
a date value.

This error occurs because the date string specified does not have any more 
tokens or separators needed to form a valid date value (composed of year, 
month, and day numerical parts) based on the DBDATE format string. For 
example, 12/15/98 is a valid date string representation when DBDATE is 
set to MDY2/. But 12/1598 is not a valid date string representation
because there are not enough separators or tokens.

To fix the problem, modify the date string representation to include a
valid format for separating the day, month, and year parts of a date
value based on the DBDATE format string setting.


-79805	No era designation found in DBDATE/GL_DATE string representation
of date value.

This error occurs because the date string specified does not have a valid 
era designation as required by the DBDATE or GL_DATE format string setting. 
For example, if DBDATE is set to Y2MDE-, but the date string representation 
specified by the user is 98-12-15, this is an error because no era
designation is at the end of the date string value.

To fix the problem, modify the date string representation to include a
valid era designation based on the DBDATE or GL_DATE format string
setting. In this example, a date string representation of 98-12-15 AD
would probably suffice, depending on the locale.


-79806	Numerical day value cannot be determined from date string
based on DBDATE.

This error occurs because the date string specified does not have a valid 
numerical day designation as required by the DBDATE format string setting. 
For example, if DBDATE is set to Y2MD-, but the date string representation 
you specify is 98-12-nn, this is an error, because nn is not a valid 
numerical day representation.

To fix the problem, modify the date string representation to include a
valid numerical day designation (1-31) based on the DBDATE format string
setting.


-79807	Numerical month value cannot be determined from date string
based on DBDATE.

This error occurs because the date string specified does not have a valid 
numerical month designation as required by the DBDATE format string setting. 
For example, if DBDATE is set to Y2MD-, but the date string representation 
you specify is 98-nn-15, this is an error, because nn is not a valid 
numerical month representation.

To fix the problem, modify the date string representation to include a
valid numerical month designation (1-12) based on the DBDATE format
string setting.


-79808	Not enough tokens specified in %D directive representation
of date string.

This error occurs because the date string specified does not have the
correct number of tokens or separators needed to form a valid date value
based on the GL_DATE %D directive (mm/dd/yy format). For example, 12/15/98
is a valid date-string representation based on the GL_DATE %D directive,
but 12/1598 is not a valid date-string representation because there are not
enough separators or tokens.

To fix the problem, modify the date-string representation to include a valid
format for the GL_DATE %D directive.


-79809	Not enough tokens specified in %x directive representation of
date string.

This error occurs because the date string specified does not have the correct
number of tokens or separators needed to form a valid date value based on 
the GL_DATE %x directive (format required is based on day, month, and year 
parts, and the ordering of these parts is determined by the specified
locale). For example, 12/15/98 is a valid date-string representation based
on the GL_DATE %x directive for the U.S. English locale, but 12/1598 is not
a valid date-string representation because there are not enough separators or
tokens.

To fix the problem, modify the date-string representation to include a valid 
format for the GL_DATE %x directive based on the locale.


-79810	This release of JDBC requires to be run with JDK 1.2+.

GBase JDBC Driver, Version 2.x, requires a JDK version of 1.2 or greater.


-79811	Connection without user/password not supported.

You called the getConnection() method for the DataSource object, and the 
user name or the password is null.

Use the user name and password arguments of the getConnection() method or
set these values in the DataSource object.


-79812	User/Password does not match with Datasource.

You called the getConnection(user, passwd) method for the DataSource 
object, and the values you supplied did not match the values already found 
in the data source.


-79813	Cannot call setBindColType() after executeQuery().

GBase JDBC Driver allows an application to specify the
output type of the resultset values so that the server can cast to that
type before returning to the client. However the output type needs to be
specified before the query is executed.This error occurs when an application
attempts to specify the output type after the query has been executed.


-79814	Blob/Clob object is either closed or invalid.

If you retrieve a smart large object using the ResultSet.getBlob() or 
ResultSet.getClob() method or create one using the IfxBlob() or IfxCblob() 
constructor, a smart large object is opened. You can then read from or write 
to the smart large object.

After you execute the IfxBlob.close() method, do not use the
smart-large-object handle for further read/write operations, or this
exception is thrown.


-79815	Not in Insert mode. Need to call moveToInsertRow() first.

You tried to use the insertRow() method, but the mode is not set to Insert.

Call the moveToInsertRow() method before you call insertRow().


-79816	Cannot determine the table name.

The table name in the query either is incorrect or refers to a table that
does not exist.


-79817	No serial, rowid, or primary key specified in the statement.

The updatable scrollable feature works only for tables that have a SERIAL 
column, a primary key, or a row ID specified in the query. If the table does
not have any of these, an updatable scrollable cursor cannot be created.


-79818	Statement concurrency type is not set to CONCUR_UPDATABLE.

You tried to call the insertRow(), updateRow(), or deleteRow() method for a 
statement that has not been created with the CONCUR_UPDATABLE concurrency
type.

Re-create the statement with this type set for the concurrency attribute.


-79819	Still in Insert mode. Call moveToCurrentRow() first.

You cannot call the updateRow() or deleteRow() method while still in Insert 
mode.

Call the moveToCurrentRow() method first.


-79820	Function contains an output parameter.

You have passed in a statement that contains an OUT parameter, but you have 
not used the drivers CallableStatement.registerOutParameter() and getXXX()
methods to process the OUT parameter.


-79821	Name unnecessary for this data type.

The data type you specified does not require a name.

Use another method that does not have a type parameter.

If you have a data type that requires a name (an opaque type or complex 
type), you must call a method that has a parameter for the name, such as the
following methods:

   public void IfxSetNull(int i, int ifxType, String name)

   public void registerOutParameter(int parameterIndex, int sqlType, 
               java.lang.String name);

   public void IfxRegisterOutParameter(int parameterIndex, int ifxType, 
               java.lang.String name);


-79822	OUT parameter has not been registered.

The function you specified using the CallableStatement interface has an OUT
parameter that has not been registered.

Call one of the registerOutParameter() or IfxRegisterOutParameter() methods
to register the OUT parameter type before you call the executeQuery() method.


-79823	IN parameter has not been set.

The function you specified using the CallableStatement interface has an IN 
parameter that has not been set.

Call the setNull() or IfxSetNull() method if you want to set a null IN
parameter. Otherwise, call one of the set methods inherited from the
PreparedStatement interface.


-79824	OUT parameter has not been set.

The function specified using the CallableStatement interface has an OUT 
parameter that has not been set.

Call the setNull() or IfxSetNull() method if you want to set a null OUT
parameter. Otherwise, call one of the set methods inherited from the
PreparedStatement interface.


-79825	Type name is required for this data type.

This data type is an opaque type, distinct type, or complex type, and it 
requires a name.

Use set methods for IN parameters and register methods for OUT parameters
that take a type name as a parameter.


-79826	Ambiguous java.sql.Type. Use IfxRegisterOutParameter().

The SQL type specified either has no mapping to an GBase data type or has 
more than one mapping.

Use one of the IfxRegisterOutParameter() methods to specify the GBase data
type.


-79827	Function does not have an output parameter.

This function does not have an OUT parameter, or this function has an OUT 
parameter whose value the server version does not return. None of the 
methods in the CallableStatement interface apply.

Use the inherited methods from the PreparedStatement interface.


-79828	Function parameter specified is not an OUT parameter.

GBase functions can have only one OUT parameter, and it is always the last
parameter.


-79829	Invalid directive used for the GL_DATE environment variable.

One or more of the directives that the GL_DATE environment variable specifies
is not allowed.

For a list of the valid directives for a GL_DATE format, see the GBase
JDBC Driver Programmer's Guide.


-79830	Insufficient information given for building a Time or Timestamp
Java object.

For correct performance of string to binary conversions for building a
java.sql.Timestamp or java.sql.Time object, all the DATETIME fields must be 
specified for the chosen date-string representation.

For java.sql.Timestamp objects, specify the year, month, day, hour, minute,
and second parts in the string representation.

For java.sql.Time objects, specify the hour, minute, and second parts in the
string representation.


-79831	Exceeded maximum number of connections configured for Connection
Pool Manager.

If you repeatedly connect to a database using a DataSource object without 
closing the connection, connections accumulate. When the total number of 
connections for the DataSource object exceeds the maximum limit (100), this 
error is thrown.


-79832	Netscape Exception! Permission to connect denied by user.

User does not have permission to connect.


-79833	Netscape Exception! Unknown exception while enabling privilege.

This error is raised by the GBase JDBC Driver when it fails to perform
a privileged operation. Check the java security policy file to make sure that
necessary privileges have been granted.


-79834	Distributed transactions (XA) not supported by this server.

This error results when an application attempts to open an XA connection
to a server that does not support distributed transactions. Ensure that the
server that the application is connecting to supports distributed transactions.


-79835	RowSet is set to ReadOnly.

This error results when an application attempts an update/insert or delete
operation on a read-only RowSet. Ensure that the RowSet in use is not read-only.


-79836	Proxy Error: No database connection.

This error is thrown by the GBase HTTP Proxy if you try to communicate 
with the database on an invalid or bad database connection.

Make sure your application has opened a connection to the database. Check your
Web server and database error logs.


-79837	Proxy Error: Input/output error while communicating with the
database.

This error is thrown by the GBase HTTP Proxy if an error is detected while
the proxy is communicating with the database. This error can occur if your 
database server is not accessible.

Make sure your database server is accessible. Check your database and Web
server error logs.


-79838	Cannot execute change permission command (chmod/attrib).

The driver is unable to change the permissions on the client JAR file. This
could happen if your client platform does not support the chmod or attrib
command, or if the user running the JDBC application does not have the
authority to change access permissions on the client JAR file.

Make sure the chmod or attrib command is available for your platform and that
the user running the application has the authority to change access
permissions on the client JAR file.


-79839	Same Jar SQL name already exists in the system catalog.

The JAR filename specified when your application called
UDTManager.createJar() has already been registered in the database server.

Use UDTMetaData.setJarFileSQLName() to specify a different SQL name for the
JAR file.


-79840	Unable to copy JAR file from client to server.

This error occurs when the pathname set using setJarTmpPath() is not writable
by user gbasedbt or the user specified in the JDBC connection.

Make sure the pathname is readable and writable by any user.


-79841	Invalid or Inconsistent Tuning Parameters for Connection Pool Datasource

The GBase JDBC Driver encountered an invalid Connection Pool Datasource
property value. Make sure that application is using valid values to set
Connection Pool Datasource properties. Refer to the GBase JDBC 
Programmer's Guide for details of valid values.


-79842	No UDR information was set in UDRMetaData.

Your application called the UDRManager.createUDRs() method without specifying
any UDRs for the database server to register.

Specify UDRs for the database server to register by calling the
UDRMetaData.setUDR() method before calling the UDRManager.createUDRs()
method.


-79843	SQL name of the JAR file was not set in UDR/UDT metaData.

Your application called either the UDTManager.createUDT() method or the
UDRManager.createUDRs() method without specifying an SQL name for the JAR
file that contains the opaque types or UDRs for the database server to
register.

Specify an SQL name for a JAR file by calling the
UDTMetaData.setJarFileSQLName() or UDRMetaData.setJarFileSQLName() method
before you call the UDTManager.createUDT() or UDRManager.createUDRs() method.


-79844	Cannot create/remove UDT/UDR as no database is specified in the
connection.

Your application created a connection without specifying a database.

The following example establishes a connection and opens a database
named "test":

   url = "jdbc:gbasedbt-sqli:myhost:1533/test:" +

   "gbasedbtserver=myserver;user=rdtest;password=test";

   conn = DriverManager.getConnection(url);

The following example establishes a connection with no database open:

   url = "jdbc:gbasedbt-sqli:myhost:1533:" +

   "gbasedbtserver=myserver;user=rdtest;password=test";

   conn = DriverManager.getConnection(url);

To resolve this problem, use the following SQL statements after the
connection is established and before you call the createUDT() or createUDRs()
method:

   Statement stmt = conn.createStatement();

   stmt.executeUpdate("create database test ...");

Alternatively, use the following code:

   stmt.executeUpdate("database test");


-79845	JAR file on the client does not exist or cannot be read.

This error occurs for one of the following reasons:

   You failed to create a client JAR file.

   You specified an incorrect pathname for the client JAR file.

   The user running the JDBC application or the user specified in the 
   connection does not have permission to open or read the client JAR file.


-79846	Invalid JAR file name.

The client JAR file your application specified as the second parameter to
UDTManager.createUDT() or UDRManager.createUDRs() must end with the .jar
extension.


-79847	The javac or jar command failed.

The driver encountered compilation errors in one of the following cases:

   Compiling .class files into .jar files, using the jar command, in response
   to a createJar() command from a JDBC application

   Compiling .java files into .class files and .jar files, using the javac
   and jar commands, in response to a call to the UDTManager.createUDTClass()
   method from a JDBC application


-79848	Same UDT SQL name already exists in the system catalog.

Your application called UDTMetaData.setSQLName() and specified a name that is
already in the database server.


-79849	UDT SQL name was not set in UDTMetaData.

Your application failed to call UDTMetaData.setSQLName() to specify an SQL
name for the opaque type.


-79850	UDT field count was not set in UDTMetaData.

Your application called UDTManager.createUDTClass() without first specifying
the number of fields in the internal data structure that defines the opaque
type.

Specify the number of fields by calling UDTMetaData.setFieldCount().


-79851	UDT length was not set in UDTMetaData.

Your application called UDTManager.createUDTClass() without first specifying
a length for the opaque type.

Specify the total length for the opaque type by calling
UDTMetaData.setLength().


-79852	UDT field name or field type was not set in UDTMetaData.

Your application called UDTManager.createUDTClass() without first specifying
a field name and data type for each field in the data structure that defines
the opaque type.

Specify each field name by calling UDTMetaData.setFieldName(). Specify a data
type for each field by calling UDTMetaData.setFieldType().


-79853	No class files to be put into the jar.

Your application called the createJar() method and passed a zero-length
string for the classnames parameter.

The method signature is as follows:

   createJar(UDTMetaData mdata, String[] classnames)


-79854	UDT java class must implement java.sql.SQLData interface.

Your application called UDTManager.createUDT() to create an opaque type whose
class definition does not implement the java.sql.SQLData interface.
UDTManager cannot create an opaque type from a class that does not implement
this interface.


-79855	Specified UDT java class is not found.

Your application called the UDTManager.createUDT() method, but the driver
could not find a class with the name you specified for the third parameter.


-79856	Specified UDT does not exists in the database.

Your application called UDTManager.removeUDT(String sqlname) to remove an
opaque type named sqlname from the database, but no opaque type with that
name exists in the database.


-79857	Invalid support function type.

This error occurs only if your application calls the
UDTMetaData.setSupportUDR() method and passes an integer other
than 0 through 7 for the type parameter. 

Use the constants defined for the support UDR types. For more information,
see the GBase JDBC Driver Programmer's Guide.


-79858	The command to remove file on the client failed.

If UDTMetaData.keepJavaFile() is not called or is set to FALSE, the driver
removes the generated .java file when the UDTManager.createUDTClass() method
executes. This error results if the driver was unable to remove the .java
file.


-79859	Invalid UDT field number.

Your application called a UDTMetaData.setXXX() or UDTMetaData.getXXX() method
and specified a field number that was less than 0 or greater than the value
set through the UDTMetaData.setFieldCount() method.


-79860	Ambiguous java type - cannot use Object/SQLData as method
argument.

One or more parameters of the method to be registered as a UDR is of type 
java.lang.Object or java.sql.SQLData. These Java data types can be mapped to
more than one GBase data type, so the driver is unable to choose a type.

Avoid using java.lang.Object or java.sql.SQLData as a method argument.


-79861	Specified UDT field type has no Java type match.

Your application called UDTMetaData.setFieldType() and specified a data type
that has no 100% match in Java. The following data types are in this
category:

   IfxTypes.IFX_TYPE_BYTE

   IfxTypes.IFX_TYPE_TEXT

   IfxTypes.IFX_TYPE_VARCHAR

   IfxTypes.IFX_TYPE_NVCHAR

   IfxTypes.IFX_TYPE_LVARCHAR

Use IFX_TYPE_CHAR or IFX_TYPE_NCHAR instead; these data types map to
java.lang.String.


-79862	Invalid UDT field type.

Your application called UDTMetaData.setFieldType() and specified an
unsupported data type for the opaque type.

For supported data types, see the GBase JDBC Driver Programmer's Guide.


-79863	UDT field length was not set in UDTMetaData.

Your application specified a field of a character, datetime, or interval
data type by calling UDTMetaData.setFieldType() but failed to specify a field
length.

Call UDTMetaData.setFieldLength() to set a field length.


-79864	Statement length exceeds the maximum.

Your application issued an SQL PREPARE, DECLARE, or EXECUTE IMMEDIATE
statement that is longer than the database server can handle. The limit
differs with different implementations but in most cases is up to 65,535
characters.

Review the program logic to ensure that an error has not caused your
application to present a string that is longer than intended. If the text has
the intended length, revise the application to present fewer statements at a
time.

This error is the same as error -460 that the database server returns.


-79865	'Statement' already closed.

This error occurs when an application attempts to call a method on a
Statement object that has already been closed.
Make sure that the Statement object has not already been closed.


-79868	ResultSet not open, operation not permitted.

This error occurs when an application attempts to call a method on a ResultSet
object that has already been closed.The method can be called only on an open 
ResultSet object. Ensure that the ResultSet object in use has not already been 
implicitly or explicitly closed.


-79877	Invalid parameter value for setting maximum field size.

This error occurs when an application attempts to set a maximum field size
to a negative value. Ensure that application is passing a non-negative integer
when calling the setMaxFieldSize() method on a Statement object.


-79878	ResultSet not open, operation 'next' not permitted. Verify that autocommit is OFF

This message appears when an application attempts to fetch a row using
a ResultSet that has been closed. Make sure that the ResultSet in use has not 
been closed, automatically or explicitly.
A ResultSet object is automatically closed when the Statement object that
generated the ResultSet object is closed, re-executed, or used to retrieve
the next result from a sequence of multiple results.


-79879	An unexpected exception was thrown.  See next exception for details.

This message appears when the JDBC driver encounters an exception that is not
anticipated in the normal course of events.Another exception chained to this 
exception contains further information regarding the unexpected error condition
that occurred.


-79880	Unable to set JDK Version for the Driver.

This message appears when the JDBC driver fails to determine which version 
of JDK is being used to run the java application. JDBC driver attempts to read 
"java.version" system property to find out which JDK version is being used.
Ensure that the JDBC driver JAR has necessary privileges to access the
"java.version" system property.


-79881	Already in local transaction, so cannot start XA transaction.

This error message appears when a JDBC application attempts to start an XA
transaction for a connection that is already associated with a local
transaction. XAResource does not support nested transactions. Before 
attempting to start an XA transaction, the application needs to ensure 
that the connection is not associated with any other transaction.


-79882	Method not supported with this server.

This error message appears when a JDBC client attempts to use functionality
that is not supported by the version of server the JDBC client is connected to.
This functionality is available in later versions of the server.
You can migrate to the latest server version to use this functionality


-79883	Class that implements IfmxPAM interface could not be located or loaded.

This error message appears when the JDBC driver fails to load the class
specified by the IFX_PAM_CLASS connection property.In order to use the PAM 
authentication mechanism, the JDBC application specifies the name of a class 
as a connection property. The JDBC driver failed to load the specified class.
Ensure that the specified class is in the CLASSPATH.


-79884	Class must implement com.gbasedbt.jdbc.IfmxPAM interface for PAM functionality.

This message appears when the class whose name has been set as a value of 
the IFX_PAM_CLASS property does not implement the com.gbasedbt.jdbc.IfmxPAM
interface.In order to use the PAM authentication mechanism, the JDBC 
application needs to set the IFX_PAM_CLASS connection property.The class 
specified as value of a IFX_PAM_CLASS connection property should Implement 
the com.gbasedbt.jdbc.IfmxPAM interface.


-79885	PAM authorization has failed.

This message appears when PAM authorization of a JDBC client fails.
Ensure that your application is responding with a correct response string
to the challenge message received from a PAM-enabled server.


-79886	PAM Response Message Size exceeds maximum size allowed.

Length of the PAM response string set by your application exceeds the
maximum allowed size. The PAM standard defines the maximum size of
a PAM message to be 512 bytes (IfxPAMChallenge.PAM_MAX_MESSAGE_SIZE).
Ensure that a correct PAM response string is supplied by your application.


-79887	Parameter name not found.

A parameter name is not found in the argument names of the specified 
stored procedure. Either one of the parameter names is not valid or 
the stored procedure name is incorrect.

Correct the parameter or procedure name and run the procedure again.

-79888	Parameters are specified by both name and ordinal position for the same CallableStatement object.

A CallableStatement object must be consistent in the way it specifies
parameters; that is, it must only use methods that take parameter names
(String objects), or it must use only methods that take parameter ordinal 
positions (integers).  A CallableStatement object that mixes the two types 
of references throws an SQLException object.

Specify parameters either by name or by ordinal for a particular 
CallableStatement object.

-79889	You cannot set a savepoint, rollback to a savepoint, or release a savepoint when the transaction is in autocommit mode.

The JDBC application attempted to create or reference a savepoint while the
autocommit transaction mode was true for the connection. Before attempting 
to set a savepoint, to rollback to a savepoint, or to release a savepoint, 
the application needs to ensure that autocommit is false. By default, 
autocommit is true in JDBC for connections to any IDS database that 
supports transaction logging.

-79890	You cannot set a savepoint, rollback to a savepoint, or release a savepoint within an XA transaction.

The JDBC application attempted to create or reference a savepoint within an
XA transaction.  Savepoints are not supported in XA transactions.

-79891	The identifier declared for the named savepoint cannot be null.

The JDBC application attempted to set a savepoint by passing NULL as the
the savepoint name. The savepoint name cannot be NULL for the connection.
setSavepoint(string) method.  Use instead the setSavepoint() method if you
want to create an unnamed savepoint.

-79892	The savepoint cannot be null when rolling back to a savepoint or releasing a savepoint.

The JDBC application attempted to rollback to a savepoint or to release a
savepoint by passing a NULL savepoint object. Only savepoint objects
that were created by the setSavepoint() methods can be passed to methods 
that rollback to a savepoint or that release a savepoint.

-79893	The savepoint is not valid in the current connection.

The JDBC application attempted to rollback to a savepoint (or to release a
savepoint) by passing an invalid savepoint object. Before the application
can rollback to a savepoint or release a savepoint, that savepoint must
first be created in the current connection.

-79894	You cannot return the numeric identifier of a named savepoint.

The JDBC application attempted to call getSavepointId() on a named
savepoint. If the savepoint was declared using the setSavepoint(string)
method, then getSavepointId() is not a valid method, and it returns this
exception. Call instead the getSavepointName() method to return the 
name of a savepoint.


-79895	You cannot return the name of an unnamed savepoint.

The JDBC application attempted to call getSavepointName() on an unnamed
savepoint. If the savepoint was  declared using the setSavepoint()
method, then getSavepointName() is not a valid method, and it returns 
this exception. Call instead the getSavepointId() method to return the 
numeric identifier of an unnamed savepoint.


-79896	Incorrect connection array index in the connection pool.

The connection array index is not a valid index. 
This is caused when the application tries to remove a connection from the 
connection pool and the array index is either negative or more than the
maximum connection size for the pool.

This problem is probably caused by the JVM running out of resources.
Check for any relevant operating system messages.


-79999	Message text will be provided in later release.

An error message with detailed message text will be provided in future
releases.


21511	Cannot request more than 1 page for online index build.

The online index build has requested more than 1 page during parallel build.

-21511	Cannot request more than 1 page for online index build.

The online index build has requested more than 1 page during parallel build.


21512	Exclusive access required to pre-image buffer.

Exclusive access required to pre-image buffer. Either a buffer was released
with BF_MODIFY flag, or it was shared or waited for by some other thread.

-21512	Exclusive access required to pre-image buffer.

Exclusive access required to pre-image buffer. Either a buffer was released
with BF_MODIFY flag, or it was shared or waited for by some other thread.


21513	Error in online index operation

Some internal error occurred during the online index operation.

-21513	Error in online index operation

Some internal error occurred during the online index operation.


21514	Error saving keyp after online index build

There was some error when saving new keyp after online index build.

-21514	Error saving keyp after online index build

There was some error when saving new keyp after online index build.


21515	Cannot perform online index build for attached indices

Online index build is allowed only for detached and semi-detached indices.
It is not supported for attached index builds.

-21515	Cannot perform online index build for attached indices

Online index build is allowed only for detached and semi-detached indices.
It is not supported for attached index builds.


21516	Partially read row

This is an internally set error on reading rows for parallel index build.
It is ignored if the parallel build is being performed for an online index

-21516	Partially read row

This is an internally set error on reading rows for parallel index build.
It is ignored if the parallel build is being performed for an online index


21517	Error allocating bufQ for preimage or updator log

Error occurred when allocating buffer queue for preimage or updator logging.

-21517	Error allocating bufQ for preimage or updator log

Error occurred when allocating buffer queue for preimage or updator logging.


21518	Error occurred while starting a thread to process preimage or updator
log.

Error occurred while starting a thread for processing preimage or updator log.

-21518	Error occurred while starting a thread to process preimage or updator
log.

Error occurred while starting a thread for processing preimage or updator log.


21519	No preimage exists

No preimage exists when one was believed to be there.

-21519	No preimage exists

No preimage exists when one was believed to be there.


21520	Bad temp partition physaddr

Bad temp partition physaddr was obtained for preimage or updator log partition.

-21520	Bad temp partition physaddr

Bad temp partition physaddr was obtained for preimage or updator log partition.


21521	More than 1 online index operation on the same table

More than 1 online index operation is not allowed on the same table.

-21521	More than 1 online index operation on the same table

More than 1 online index operation is not allowed on the same table.


21522	No online index build possible

No online index build is possible in the requested scenario.
The index build requires reading rows one by one, or online index builds
are not allowed in this IDS edition.

-21522	No online index build possible

No online index build is possible in the requested scenario.
The index build requires reading rows one by one, or online index builds
are not allowed in this IDS edition.


21523	Cannot proceed with a dirty/modified table data dictionary entry.

DML operations are susceptible to this error when they are referencing an
older copy of the table's data dictionary information.
Most likely, the database server just completed a 'CREATE/DROP INDEX ... '
operation on the table referred to by the DML operation.

This error is accompanied by SQL Error -710.

-21523	Cannot proceed with a dirty/modified table data dictionary entry.

DML operations are susceptible to this error when they are referencing an
older copy of the table's data dictionary information.
Most likely, the database server just completed a 'CREATE/DROP INDEX ... '
operation on the table referred to by the DML operation.

This error is accompanied by SQL Error -710.

21524	Cannot create a temporary operating system file to use in a sort

A sort was being performed that did not use a dbspace partition to store
overflow data. Overflow data is written to a temporary operating system 
file. However, the database server could not create a temporary operating
system file for this sort. Consider setting the PSORT_DBTEMP environment
variable to a secure directory with a minimum of 200 MB of free space. 
If PSORT_DBTEMP is not set then the database server stores overflow data
in the /tmp or $GBASEDBTDIR/tmp directory.

-21524	Cannot create a temporary operating system file to use in a sort

A sort was being performed that did not use a dbspace partition to store
overflow data. Overflow data is written to a temporary operating system 
file. However, the database server could not create a temporary operating
system file for this sort. Consider setting the PSORT_DBTEMP environment 
variable to a secure directory with a minimum of 200 MB of free space. 
If PSORT_DBTEMP is not set then the database server stores overflow data
in the /tmp or $GBASEDBTDIR/tmp directory.

21525	Cannot write to a temporary operating system file during a sort

A sort was being performed that did not use a dbspace partition to store
overflow data. Overflow data is written to a temporary operating
system file. However, the database server could not write to this 
temporary operating system file, most likely because the file directory
is out of space. The location of this file is in the path specified by
the PSORT_DBTEMP environment variable. If PSORT_DBTEMP is not set then 
the database server puts these temporary files in the /tmp or
$GBASEDBTDIR/tmp directory.

-21525	Cannot write to a temporary operating system file during a sort

A sort was being performed that did not use a dbspace partition to store
overflow data. Overflow data is written to a temporary operating
system file. However, the database server could not write to this 
temporary operating system file, most likely because the file directory 
is out of space. The location of this file is in the path specified by
the PSORT_DBTEMP environment variable. If PSORT_DBTEMP is not set then 
the database server puts these temporary files in the /tmp or
$GBASEDBTDIR/tmp directory.

21526	This log cannot be dropped, because the next log has an open transaction.

This error occurs when a log is requested to be dropped, but cannot be
because the next log contains the beginning of an open transaction. This log
is required to roll back that transaction, explicitly or implicitly during
long transaction abort. Also, the remaining log space might not be
sufficient to hold the rollback log records.

-21526	This log cannot be dropped, because the next log has an open transaction.

This error occurs when a log is requested to be dropped, but cannot be
because the next log contains the beginning of an open transaction. This log
is required to roll back that transaction, explicitly or implicitly during
long transaction abort. Also, the remaining log space might not be
sufficient to hold the rollback log records.

21527	The partition could not be created because the dbspace is currently unavailable.

The partition could not be created in the specified dbspace because that dbspace
is either disabled, in the process of being restored from a backup, or in the
process of being updated as part of fast recovery.

Enable the dbspace or wait for it to complete its recovery, and then run the
command to create the partition again.

-21527	The partition could not be created because the dbspace is currently unavailable.

The partition could not be created in the specified dbspace because that dbspace
is either disabled, in the process of being restored from a backup, or in the
process of being updated as part of fast recovery.

Enable the dbspace or wait for it to complete its recovery, and then run the
command to create the partition again.

-26014	Alter fragment on typed table is not allowed.

Alter fragment on table/index is not supported for typed table. 

-26015	All fragments of the table or index need to be of same pagesize.

This error can occur on creating a fragmented table or index across dbspaces
of different pagesize. It can also occur while performing an ALTER FRAGMENT
on a table or index and the new dbspaces are of different pagesize than the 
dbspaces in the existing fragmentation strategy. For an interval fragmented
table or index, this error can occur if the pagesize of the dbspaces in the
STORE IN clause is different from the pagesize of initial fragment dbspaces.


-26016	Illegal leading byte 0x20 in Index name (index_name).

In some situations, such as when a constraint is defined, the database
server creates indexes internally. These indexes, by convention, always
have a leading ASCII blank (hex 20) as the first byte of their name. To
avoid a conflict, user-created indexes cannot have an ASCII blank (hex 20)
as the first byte of their name. Also, there can be no reference to indexes
with a leading blank in any user-issued statements. This rule is enforced
regardless of the locale. This rule is also enforced regardless of the use
of the DELIMIDENT environment variable.

-26017	External indices are not supported with non-default pagesizes.

Virtual-Index Interface (VII) does not support creating index on non-default
pagesize dbspaces. This error can occur while creating a R-Tree index (which
is part of the server and internally implemented using VII), or when creating
any index which was implemented using VII.


32000	The rollback was caused by an unspecified reason.

This error is same as XA_RBROLLBACK in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, the Resource Manager rolled back the transaction 
branch for an unspecified reason. The Resource Manager did not commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.
This value can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager marked the transaction branch 
rollback-only for an unspecified reason. The Resource Manager has dissociated 
the transaction branch from the thread of control and has marked rollback-only 
the work performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager rolled back the transaction 
branch for an unspecified reason. The Resource Manager did not prepare to 
commit the work done on behalf of the transaction branch. Upon return, the 
Resource Manager has rolled back the branch's work and has released all held 
resources.

In the xa_rollback context, the Resource Manager rolled back the transaction 
branch for an unspecified reason. The Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager marked the transaction branch 
rollback-only for an unspecified reason. The Resource Manager has not 
associated the transaction branch with the thread of control and has 
marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32000	The rollback was caused by an unspecified reason.

This error is same as XA_RBROLLBACK in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, the Resource Manager rolled back the transaction 
branch for an unspecified reason. The Resource Manager did not commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.
This value can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager marked the transaction branch 
rollback-only for an unspecified reason. The Resource Manager has dissociated 
the transaction branch from the thread of control and has marked rollback-only 
the work performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager rolled back the transaction 
branch for an unspecified reason. The Resource Manager did not prepare to 
commit the work done on behalf of the transaction branch. Upon return, the 
Resource Manager has rolled back the branch's work and has released all held 
resources.

In the xa_rollback context, the Resource Manager rolled back the transaction 
branch for an unspecified reason. The Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager marked the transaction branch 
rollback-only for an unspecified reason. The Resource Manager has not 
associated the transaction branch with the thread of control and has 
marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32001	The rollback was caused by a communication failure.

This error is same as XA_RBCOMMFAIL in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

A communication failure occurred within the Resource Manager.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32001	The rollback was caused by a communication failure.

This error is same as XA_RBCOMMFAIL in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

A communication failure occurred within the Resource Manager.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32002	The rollback was caused by a deadlock was detected.

This error is same as XA_RBDEADLOCK in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The Resource Manager detected a deadlock.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32002	The rollback was caused by a deadlock was detected.

This error is same as XA_RBDEADLOCK in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The Resource Manager detected a deadlock.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32003	A condition that violates the integrity of the resources was detected.

This error is same as XA_RBINTEGRITY in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The Resource Manager detected a violation of the integrity of its resources.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32003	A condition that violates the integrity of the resources was detected.

This error is same as XA_RBINTEGRITY in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The Resource Manager detected a violation of the integrity of its resources.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32004	The Resource Manager rolled back the transaction branch for a reason not on the XA rollback errors.

This error is same as XA_RBOTHER in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, the Resource Manager rolled back the transaction 
branch for a reason not on the XA rollback errors. The Resource Manager 
did not commit the work done on behalf of the transaction branch. Upon return, 
the Resource Manager has rolled back the branch's work and has released all 
held resources. This value can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager marked the transaction branch 
rollback-only for a reason not on the XA rollback errors. The Resource Manager 
has dissociated the transaction branch from the thread of control and has 
marked rollback-only the work performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager rolled back the transaction 
branch for a reason not on the XA rollback errors. The Resource Manager did 
not prepare to commit the work done on behalf of the transaction branch. Upon 
return, the Resource Manager has rolled back the branch's work and has released
all held resources.

In the xa_rollback context, the Resource Manager rolled back the transaction 
branch for a reason not on the XA rollback errors. The Resource Manager has 
rolled back the transaction branch's work and has released all held resources. 
These values are typically returned when the branch was already marked 
rollback-only.

In the xa_start context, the Resource Manager marked the transaction branch 
rollback-only for a reason not on the XA rollback errors. The Resource Manager 
has not associated the transaction branch with the thread of control and has 
marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32004	The Resource Manager rolled back the transaction branch for a reason not on the XA rollback errors.

This error is same as XA_RBOTHER in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, the Resource Manager rolled back the transaction 
branch for a reason not on the XA rollback errors. The Resource Manager 
did not commit the work done on behalf of the transaction branch. Upon return, 
the Resource Manager has rolled back the branch's work and has released all 
held resources. This value can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager marked the transaction branch 
rollback-only for a reason not on the XA rollback errors. The Resource Manager 
has dissociated the transaction branch from the thread of control and has 
marked rollback-only the work performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager rolled back the transaction 
branch for a reason not on the XA rollback errors. The Resource Manager did 
not prepare to commit the work done on behalf of the transaction branch. Upon 
return, the Resource Manager has rolled back the branch's work and has released
all held resources.

In the xa_rollback context, the Resource Manager rolled back the transaction 
branch for a reason not on the XA rollback errors. The Resource Manager has 
rolled back the transaction branch's work and has released all held resources. 
These values are typically returned when the branch was already marked 
rollback-only.

In the xa_start context, the Resource Manager marked the transaction branch 
rollback-only for a reason not on the XA rollback errors. The Resource Manager 
has not associated the transaction branch with the thread of control and has 
marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32005	A protocol error occurred in the Resource Manager.

This error is same as XA_RBPROTO in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

A protocol error occurred within the Resource Manager.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32005	A protocol error occurred in the Resource Manager.

This error is same as XA_RBPROTO in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

A protocol error occurred within the Resource Manager.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32006	The rollback was caused by a transaction branch took too long.

This error is same as XA_RBTIMEOUT in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The work represented by this transaction branch took too long.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32006	The rollback was caused by a transaction branch took too long.

This error is same as XA_RBTIMEOUT in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The work represented by this transaction branch took too long.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32007	The Resource Manager detected transient error.

This error is same as XA_RBTRANSIENT in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The Resource Manager has detected a transient error.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


-32007	The Resource Manager detected transient error.

This error is same as XA_RBTRANSIENT in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The Resource Manager detected a transient error.

In the xa_commit context, the Resource Manager did not commit the work done 
on behalf of the transaction branch. Upon return, the Resource Manager has 
rolled back the branch's work and has released all held resources. This value 
can be returned only if TMONEPHASE is set in flags.

In the xa_end context, the Resource Manager has dissociated the transaction 
branch from the thread of control and has marked rollback-only the work 
performed on behalf of *xid. 

In the xa_prepare context, the Resource Manager did not prepare to commit the 
work done on behalf of the transaction branch. Upon return, the Resource 
Manager has rolled back the branch's work and has released all held resources.

In the xa_rollback context, the Resource Manager has rolled back the 
transaction branch's work and has released all held resources. These values 
are typically returned when the branch was already marked rollback-only.

In the xa_start context, the Resource Manager has not associated the 
transaction branch with the thread of control and has marked *xid rollback-only.

Also refer to the Resource Manager guide for more details.


32009	Routine returned with no effect and may  be re-issued.

This error is same as XA_RETRY in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, the Resource Manager is not able to commit the 
transaction branch at this time.  This value can be returned when a blocking 
condition exists and TMNOWAIT was set. Note, however, that this value can 
also be returned even when TMNOWAIT is not set (for example, if the necessary 
stable storage is currently unavailable). This value cannot be returned if 
TMONEPHASE is set in flags. All resources held on behalf of *xid remain in a 
prepared state until commitment is possible. The Transaction Manager should 
re-issue xa_commit at a later time.

In the xa_complete context, TMNOWAIT was set in flags and no asynchronous 
operation has completed.

In the xa_start context, TMNOWAIT was set in flags and a blocking condition 
exists.

Also refer to the Resource Manager guide for more details.


-32009	Routine returned with no effect and may  be re-issued.

This error is same as XA_RETRY in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, the Resource Manager is not able to commit the 
transaction branch at this time.  This value can be returned when a blocking 
condition exists and TMNOWAIT was set. Note, however, that this value can 
also be returned even when TMNOWAIT is not set (for example, if the necessary 
stable storage is currently unavailable). This value cannot be returned if 
TMONEPHASE is set in flags. All resources held on behalf of *xid remain in a 
prepared state until commitment is possible. The Transaction Manager should 
re-issue xa_commit at a later time.

In the xa_complete context, TMNOWAIT was set in flags and no asynchronous 
operation has completed.

In the xa_start context, TMNOWAIT was set in flags and a blocking condition 
exists.

Also refer to the Resource Manager guide for more details.


32010	The transaction branch has been heuristically committed and rolled back.

This error is same as XA_HEURMIX in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was partially committed and 
partially rolled back.

In the xa_rollback context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was partially committed and 
partially rolled back. A Resource Manager can return this value only if it 
has successfully prepared *xid.

Also refer to the Resource Manager guide for more details.


-32010	The transaction branch has been heuristically committed and rolled back.

This error is same as XA_HEURMIX in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was partially committed and 
partially rolled back.

In the xa_rollback context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was partially committed and 
partially rolled back. A Resource Manager can return this value only if it 
has successfully prepared *xid.

Also refer to the Resource Manager guide for more details.


32011	The transaction branch has been heuristically rolled back.

This error is same as XA_HEURRB in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was rolled back.

In the xa_rollback context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was rolled back. A Resource 
Manager can return this value only if it has successfully prepared *xid.

Also refer to the Resource Manager guide for more details.


-32011	The transaction branch has been heuristically rolled back.

This error is same as XA_HEURRB in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was rolled back.

In the xa_rollback context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was rolled back. A Resource 
Manager can return this value only if it has successfully prepared *xid.

Also refer to the Resource Manager guide for more details.


32012	The transaction branch has been heuristically committed.

This error is same as XA_HEURCOM in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was committed.

In the xa_rollback context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was committed. A Resource 
Manager can return this value only if it has successfully prepared *xid.

Also refer to the Resource Manager guide for more details.


-32012	The transaction branch has been heuristically committed.

This error is same as XA_HEURCOM in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was committed.

In the xa_rollback context, due to a heuristic decision, the work done on 
behalf of the specified transaction branch was committed. A Resource 
Manager can return this value only if it has successfully prepared *xid.

Also refer to the Resource Manager guide for more details.


32013	The transaction branch might have been heuristically completed.

This error is same as XA_HEURHAZ in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to some failure, the work done on behalf of 
the specified transaction branch might have been heuristically completed.

In the xa_rollback context, due to some failure, the work done on behalf of 
the specified transaction branch might have been heuristically completed. A 
Resource Manager can return this value only if it has successfully prepared 
*xid.

Also refer to the Resource Manager guide for more details.


-32013	The transaction branch might have been heuristically completed.

This error is same as XA_HEURHAZ in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit context, due to some failure, the work done on behalf of 
the specified transaction branch might have been heuristically completed.

In the xa_rollback context, due to some failure, the work done on behalf of 
the specified transaction branch might have been heuristically completed. A 
Resource Manager can return this value only if it has successfully prepared 
*xid.

Also refer to the Resource Manager guide for more details.


32014	Resumption must occur where suspension occurred.

This error is same as XA_NOMIGRATE in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_end context, the Resource Manager was unable to prepare the 
transaction context for migration.  However, the Resource Manager has 
suspended the association. The Transaction Manager can resume the 
association only in the current thread. This code can be returned only 
when both TMSUSPEND and TMMIGRATE are set in flags. A Resource Manager 
that sets TMNOMIGRATE in the flags element of its xa_switch_t structure 
need not return [XA_NOMIGRATE].

Also refer to the Resource Manager guide for more details.


-32014	Resumption must occur where suspension occurred.

This error is same as XA_NOMIGRATE in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_end context, the Resource Manager was unable to prepare the 
transaction context for migration.  However, the Resource Manager has 
suspended the association. The Transaction Manager can resume the 
association only in the current thread. This code can be returned only 
when both TMSUSPEND and TMMIGRATE are set in flags. A Resource Manager 
that sets TMNOMIGRATE in the flags element of its xa_switch_t structure 
need not return [XA_NOMIGRATE].

Also refer to the Resource Manager guide for more details.


32015	Asynchronous operation already outstanding.

This error is same as XAER_ASYNC in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

TMASYNC was set in flags, and either the maximum number of outstanding
asynchronous operations has been exceeded, or TMUSEASYNC is not set in the
flags element of the Resource Manager's xa_switch_t structure.

Also refer to the Resource Manager guide for more details.


-32015	Asynchronous operation already outstanding.

This error is same as XAER_ASYNC in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

TMASYNC was set in flags, and either the maximum number of outstanding
asynchronous operations has been exceeded, or TMUSEASYNC is not set in the
flags element of the Resource Manager's xa_switch_t structure.

Also refer to the Resource Manager guide for more details.


32016	An Resource Manager error occurred in the transaction branch.

This error is same as XAER_RMERR in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_close context, an error occurred when closing the resource.

In the xa_commit context, an error occurred in committing the work performed 
on behalf of the transaction branch and the branch's work has been rolled back.
Note that returning this error signals a catastrophic event to a Transaction 
Manager because other Resource Managers might successfully commit their work on 
behalf of this branch. This error should be returned only when a Resource 
Manager concludes that it can never commit the branch and that it cannot hold 
the branch's resources in a prepared state. Otherwise, [XA_RETRY] should be 
returned.

In the xa_end context, an error occurred in dissociating the transaction 
branch from the thread of control.

In the xa_forget context, an error occurred in the Resource Manager and the 
Resource Manager has not forgotten the transaction branch.

In the xa_open context, an error occurred when opening the resource.

In the xa_prepare context, the Resource Manager encountered an error in 
preparing to commit the transaction branch's work. The specified XID might or 
might not have been prepared.

In the xa_recover context, an error occurred in determining the XIDs to return.

In the xa_rollback context, an error occurred in rolling back the transaction 
branch. The Resource Manager is free to forget about the branch when returning 
this error so long as all accessing threads of control have been notified of 
the branch's state.

In the xa_start context, an error occurred in associating the transaction 
branch with the thread of control.

Also refer to the Resource Manager guide for more details.


-32016	An Resource Manager error occurred in the transaction branch.

This error is same as XAER_RMERR in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_close context, an error occurred when closing the resource.

In the xa_commit context, an error occurred in committing the work performed 
on behalf of the transaction branch and the branch's work has been rolled back.
Note that returning this error signals a catastrophic event to a Transaction 
Manager because other Resource Managers might successfully commit their work on 
behalf of this branch. This error should be returned only when a Resource 
Manager concludes that it can never commit the branch and that it cannot hold 
the branch's resources in a prepared state. Otherwise, [XA_RETRY] should be 
returned.

In the xa_end context, an error occurred in dissociating the transaction 
branch from the thread of control.

In the xa_forget context, an error occurred in the Resource Manager and the 
Resource Manager has not forgotten the transaction branch.

In the xa_open context, An error occurred when opening the resource.

In the xa_prepare context, the Resource Manager encountered an error in 
preparing to commit the transaction branch's work. The specified XID might or 
might not have been prepared.

In the xa_recover context, an error occurred in determining the XIDs to return.

In the xa_rollback context, an error occurred in rolling back the transaction 
branch. The Resource Manager is free to forget about the branch when returning 
this error so long as all accessing threads of control have been notified of 
the branch's state.

In the xa_start context, an error occurred in associating the transaction 
branch with the thread of control.

Also refer to the Resource Manager guide for more details.


32017	The XID is not valid.

This error is same as XAER_NOTA in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit, xa_end, xa_prepare, xa_rollback context, the specified XID is
not known by the Resource Manager.

In the xa_forget context, the specified XID is not known by the Resource 
Manager as a heuristically completed XID.
 
In the xa_start context, either TMRESUME or TMJOIN was set in flags, and the 
specified XID is not known by the Resource Manager.

Also refer to the Resource Manager guide for more details.


-32017	The XID is not valid.

This error is same as XAER_NOTA in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit, xa_end, xa_prepare, xa_rollback context, the specified XID is
not known by the Resource Manager.

In the xa_forget context, the specified XID is not known by the Resource 
Manager as a heuristically completed XID.
 
In the xa_start context, either TMRESUME or TMJOIN was set in flags, and the 
specified XID is not known by the Resource Manager.

Also refer to the Resource Manager guide for more details.


32018	Invalid arguments were given.

This error is same as XAER_INVAL in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_close, xa_commit, xa_complete, xa_end, xa_forget, xa_open, 
xa_prepare, xa_rollback, or xa_start context, invalid arguments were specified.

In the xa_recover context, the pointer xids is NULL and count is greater 
than 0, count is negative, or an invalid flags was specified, or the thread of 
control does not have a recovery scan open and did not specify TMSTARTRSCAN 
in flags.

Also refer to the Resource Manager guide for more details.


-32018	Invalid arguments were given.

This error is same as XAER_INVAL in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_close, xa_commit, xa_complete, xa_end, xa_forget, xa_open, 
xa_prepare, xa_rollback, or xa_start context, invalid arguments were specified.

In the xa_recover context, the pointer xids is NULL and count is greater 
than 0, count is negative, or an invalid flags was specified, or the thread of 
control does not have a recovery scan open and did not specify TMSTARTRSCAN 
in flags.

Also refer to the Resource Manager guide for more details.


32019	Routine invoked in improper context.

This error is same as XAER_PROTO in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The routine was invoked in an improper context.

Also refer to the Resource Manager guide for more details.


-32019	Routine invoked in improper context.

This error is same as XAER_PROTO in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

The routine was invoked in an improper context.

Also refer to the Resource Manager guide for more details.


32020	Resource Manager unavailable.

This error is same as XAER_RMFAIL in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit, xa_end, xa_forget, xa_recover, xa_rollback, xa_start context,
An error occurred that makes the Resource Manager unavailable.

In the xa_prepare context, an error occurred that makes the Resource Manager 
unavailable. The specified XID might or might not have been prepared.

Also refer to the Resource Manager guide for more details.


-32020	Resource Manager unavailable.

This error is same as XAER_RMFAIL in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_commit, xa_end, xa_forget, xa_recover, xa_rollback, xa_start context,
An error occurred that makes the Resource Manager unavailable.

In the xa_prepare context, an error occurred that makes the Resource Manager 
unavailable. The specified XID might or might not have been prepared.

Also refer to the Resource Manager guide for more details.


32021	The XID already exists.

This error is same as XAER_DUPID in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_start context, if neither TMRESUME nor TMJOIN was set in flags 
(indicating the initial use of *xid) and the XID already exists within the 
Resource Manager, the Resource Manager must return [XAER_DUPID]. The Resource 
Manager failed to associate the transaction branch with the thread of control.

Also refer to the Resource Manager guide for more details.


-32021	The XID already exists.

This error is same as XAER_DUPID in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_start context, if neither TMRESUME nor TMJOIN was set in flags 
(indicating the initial use of *xid) and the XID already exists within the 
Resource Manager, the Resource Manager must return [XAER_DUPID]. The Resource 
Manager failed to associate the transaction branch with the thread of control.

Also refer to the Resource Manager guide for more details.


32022	Resource Manager doing work outside global transaction.

This error is same as XAER_OUTSIDE in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_start context, the Resource Manager is doing work outside any global 
transaction on behalf of the application.

Also refer to the Resource Manager guide for more details.


-32022	Resource Manager doing work outside global transaction.

This error is same as XAER_OUTSIDE in the X/OPEN XA Specification Standard. 
For more information, refer to the "Distributed Transaction Processing: 
The XA Specification". 

According to the X/OPEN XA Specification Standard :

In the xa_start context, the Resource Manager is doing work outside any global 
transaction on behalf of the application.

Also refer to the Resource Manager guide for more details.


32023	Error in executing an xadatasource purpose routine execution sequence.

An error occurred when an attempt was made to execute an xadatasource purpose
routine. Try the statement again.


-32023	Error in executing an xadatasource purpose routine execution sequence.

An error occurred when an attempt was made to execute an xadatasource purpose
routine. Try the statement again.


32024	Error indicated by an xadatasource purpose routine.

An xadatasource purpose routine returned MI_ERROR.


-32024	Error indicated by an xadatasource purpose routine.

An xadatasource purpose routine returned MI_ERROR.


32025	Improper return value from xadatasource purpose routine.

An xadatasource purpose routine returned a value that is not valid. Possibly a
bug exists in the xadatasource purpose routine.


-32025	Improper return value from xadatasource purpose routine.

An xadatasource purpose routine returned a value that is not valid. Possibly a
bug exists in the xadatasource purpose routine.


-26018	Table %s has a referential key constraint and is not empty.

The table being truncated has a unique or primary key that is referenced by
the above table. 


-26019	Purpose function am_truncate not defined for table or index.

The am_truncate purpose function is not defined for the VTI table or 
a VII index on the table being truncated.


-26020	Truncating a table with delete trigger requires ALTER privilege.

You cannot truncate a table that has delete triggers unless you have ALTER
privilege. A user with DELETE privilege can truncate such a table only
if there are no delete triggers active.


-26021	No operations allowed after truncate or online alter fragment in a transaction.

The only allowed operations in a transaction after a truncate or online alter
fragment are COMMIT and ROLLBACK.


-26022	EXTERNAL NAME too long.

The EXTERNAL NAME clause of the CREATE FUNCTION or CREATE PROCEDURE 
cannot contain more than 255 characters.  

26023	Cannot perform rename operation while non-mastered or strict mastered -- column and table names must match along with data types across all replicate participants -- replicates are defined for it

Rename table and column operations are allowed only on Enterprise Replication 
tables that are defined with non-strict mastered replicates. 
With non-strict mastered replicates, column names can be different but 
column data types must match. Non-strict mastered replicates can be defined 
with --name=n attribute of 'cdr define repl' or with 'cdr modify repl' commands.
For more information about non-strict mastered replicates, mastered replicates
and non-mastered replicates see Enterprise Replication Manual.

-26023	Cannot perform rename operation while non-mastered or strict mastered -- column and table names must match along with data types across all replicate participants -- replicates are defined for it

Rename table and column operations are allowed only on Enterprise Replication 
tables that are defined with non-strict mastered replicates. 
With non-strict mastered replicates, column names can be different but 
column data types must match. Non-strict mastered replicates can be defined 
with --name=n attribute of 'cdr define repl' or with 'cdr modify repl' commands.
For more information about non-strict mastered replicates, mastered replicates
and non-mastered replicates see Enterprise Replication Manual.


-26024	The sysdbopen and sysdbclose routines cannot have arguments or return values.

The SPL routines sysdbopen and sysdbclose must be defined without any arguments
or return values.

-26025	SELECT FROM INSERT syntax is disallowed in this context.

SELECT FROM INSERT syntax is not allowed in the following cases
    * SELECT statement is part of join or is nested as a subquery.
    * SELECT statement is part of CREATE VIEW statement.
    * SELECT statement appears as a subquery either in query predicates or projection
      list.
    * SELECT statement appears as part of INSERT INTO SELECT statement.


-26026	Remote table reference in INSERT statement disallowed in SELECT FROM INSERT syntax.

SELECT FROM INSERT statement does not support remote object references in the 
INSERT part of statement. Please rewrite the query so that there are no remote 
object references in the INSERT part of the statement.

-26027	Label label-name specified on  a GOTO statement is not valid.

An undefined or invalid label is used with  the GOTO statement.
The GOTO label must be defined within the same procedure.
Check the label-name for correct spelling.

-26028	Label label-name must be unique within an SPL procedure.

The scope of the label-name is a procedure, so the label names must be unique
between the CREATE [PROCEDURE|FUNCTION] and END [PROCEDURE|FUNCTION] keywords.

-26029	GOTO cannot be used, and labels cannot be defined, within an exception h
andler.

Rewrite the exception handler block so that it includes no GOTO statements.

Example of error:

        CREATE PROCEDURE testproc()
        BEGIN
        ON EXCEPTION IN (-206)
        <<ex_label>>                             -- error
        CREATE TABLE emp_list (lname CHAR(15));
        GOTO ex_label;                           -- error
        INSERT INTO emp_list VALUES ('john');
        END EXCEPTION WITH RESUME
        INSERT INTO emp_list VALUES ('john');
        END
        ...

        ...
        END PROCEDURE;

-26030	Invalid usage of EXIT or CONTINUE statement, which should be within a st
atement loop.

Within the SPL routine,EXIT and CONTINUE statements must be placed within
a WHILE, FOR or LOOP statement loop.

-26031	END LOOP label label-name doesn't not match with the LOOP label label-na
me.

The LOOP lable-name and END LOOP label-name do not match.
Check the label-names for correct spelling.

-26032	Invalid label label-name used with EXIT WHEN clause.

The label is not defined or is not a valid label for the LOOP statement.
The EXIT WHEN label must be defined within a scope that the LOOP
statement can reach.  Check the label-names for correct spelling.


-26041	Invalid values specified for the %s environment variable.

An invalid value was provided for the environment variable. Please check your user documentation, and provide the correct value, and try again.

-26042	Function (explain_sql) failed in %s.

In the explain_sql UDR, the execution failed in the specified function.

-26043	Function (explain_sql): The required parameter %s is NULL

The required parameter is missing when you execute the explain_sql UDR.

-26044	Function (explain_sql): An error occurred during the reading of the parameter %s.  

An error occurred during the reading of the parameter.

-26045	Warning: Function (explain_sql) %s and the current support version does not match.

The provided major_version or minor_version of explain_sql does not match the current server version. Please check your client version and make the necessary adjustment.
 
-26046	Warning: Function (explain_sql): The requested locale was not provided. The default locale %s will be used. 

Because a locale was not provided in explain_sql UDR, the default locale will be used in the query.

-26047	Function (explain_sql) does not support the query provided in (%s). Only a single select statement is supported.

Explain_sql only supports a single SELECT statement. Please change your query.

-26048	Function (explain_sql) has an invalid parameter %s parameter.

The value you provided for the parameter is invalid.

-26049	Function (explain_sql) has invalid encoding (%s) for xml_input.

Xml_input should be in UTF-8 encoding.

-26050	Function (explain_sql) does not support a query with host variables.

Explain_sql only supports a single SELECT statement without any host variables. Please change your query.

-84500	Attempt to update a stale version of a row

An attempt was made to update a stale copy of a row.  This caused a
optimistic concurrancy failure.

This error can occur when using updates on secondary and the current
verstion of the row has not yet been replicated to the secondary on which
the client application is connected.

-84501	Connection between secondary and primary has been lost

The connection between the secondary and the primary has been lost.  This
will prevent the updates on secondary until connectivity has been
reestablished.

-26036	Cannot use the WITH VERCOLS clause because the table already has version columns.

The table already has version columns created by the WITH VERCOLS clause. You cannot add additional version columns.

-26037	Cannot drop VERCOLS when table does not have version columns.

The table that you were attempting to alter does not have VERCOLS columns.

-26051	EXECUTE IMMEDIATE and PREPARE cannot take NULL statement

Expression that specifies SQL statement text in either EXECUTE IMMEDIATE 
or PREPARE evaluates to a NULL value. Make sure to pass the text of a
valid SQL statement to these statements.

This error can be prevented by testing the expression passed to either
EXECUTE IMMEDIATE or PREPARE for a NULL value:

    Example (1)

        DEFINE qStr CHAR(255);
        ...
        LET qStr = <expression>;

        -- validate if <expression> above evalutes to NULL
        IF (qStr IS NOT NULL) THEN
            EXECUTE IMMEDIATE qStr;
        ELSE
            ...
        END IF;

    Example (2)

        DEFINE def_lc1 INTEGER;
        DEFINE lc1 INTEGER;

        LET def_lc1 = 10;

        SELECT c1 INTO lc1 FROM t1 WHERE c2 = 10;

        -- validate if query above returns a NULL value to lc1
        -- if it is NULL then logic below should use default 
        -- lc1 (def_lc1) to avoid concatenation that returns NULL

        IF (lc1 IS NOT NULL) THEN
            PREPARE stmtId FROM "select c2[" || lc1 || "] FROM t2";
        ELSE
            PREPARE stmtId FROM "select c2[" || def_lc1 || "] FROM t2";
        END IF;

-26052	OPEN attempted on already opened cursor (%s)

This OPEN statement referenced a cursor that has already been opened. Review
the program logic, and CLOSE the cursor before attempting to reopen it.

Example below makes sure to CLOSE the cursor before trying to OPEN the
same cursor with different input parameter values:

    Example

        OPEN c_emp USING lgivenname;
        ...
        CLOSE c_emp;
        OPEN c_emp USING lfamilyname;
        ...
        CLOSE c_emp;

-26053	FETCH or CLOSE cannot reference cursor (%s) that is not opened

The FETCH or CLOSE statement attempted to use an unopened cursor.
Make sure to OPEN the cursor before attempting FETCH or CLOSE that 
cursor.

    Example

        OPEN c_cust USING lfname, llname;
        ...
        FETCH c_cust INTO lcustomer_num, lcompany;
        ...
        CLOSE c_cust;

-26054	Cannot FREE a cursor (%s) that is in use

This FREE statement attempts to free a cursor that is still in use 
(i.e. not closed yet). Make sure to close this cursor by using the
CLOSE statement before a FREE attempt.

    Example

        DECLARE c_cust CURSOR FOR s_cust;

        OPEN c_cust USING lfname, llname;
        FETCH c_cust INTO lcustomer_num, lcompany;
        CLOSE c_cust;

        FREE c_cust;

-26055	Either statement-id or cursor name (%s) is not defined

This error occurs when either statement-id or a cursor name is not
defined before attempting to use it. Make sure to either PREPARE 
the statement-id or DECLARE the cursor before using it.

-26056	Function cursor (%s) in SPL cannot have WITH HOLD option

In SPL routines, the WITH HOLD option is not allowed for a function 
cursor declared for the EXECUTE PROCEDURE or EXECUTE FUNCTION statement. 
Omit the WITH HOLD keywords while declaring this cursor.

Only ESQL/C routines support function cursors WITH HOLD.

-26057	Either statement-id or cursor (%s) is already in use

This error can be returned in either of the following two cases:

    Case (1)

        Attempt to PREPARE a statement with the same identifier that
        an earlier PREPARE or DECLARE statement declared, and that has
        not yet been freed for re-use. Review the program logic, and 
        either FREE this identifier before using it again for this 
        PREPARE statement, or else specify a different identifier.

        Example below demonstrates possible way to avoid this error:

          PREPARE s_emp  FROM "select givenname from employee";
          PREPARE s_cust FROM "select fname from customer";
          ...
          FREE s_cust;
          PREPARE s_cust FROM "select lname from customer";
          ...
          FREE s_cust;
          FREE s_emp;

    Case (2)

        Attempt to DECLARE a cursor with the same identifier that an
        earlier PREPARE or DECLARE statement declared, and that has not
        yet been freed for re-use. Review the program logic, and either 
        FREE this identifier before using it again for this DECLARE
        statement, or else specify a different identifier.

        Example below demonstrates possible way to avoid this error:

          DECLARE c_emp CURSOR FOR s_emp;
          DECLARE c_cust CURSOR FOR s_cust;
          ...
          FREE c_emp;
          DECLARE c_cust CURSOR FOR s_cust;
          ...
          FREE c_cust;
          FREE c_emp;

-26058	EXECUTE IMMEDIATE and PREPARE in SPL cannot allow multiple SQL statements

Either EXECUTE IMMEDIATE or PREPARE statement in an SPL routine is trying 
to process an expression that evaluates to the text of more than one SQL
statement. Change your program logic to pass to the EXECUTE IMMEDIATE or
PREPARE statement an expression or a quoted string that evaluates to the
text of only a single SQL statement.

In an SPL routine, the following examples are not valid because the EXECUTE
IMMEDIATE or PREPARE statement specifies the text of more than one SQL
statement:

   Case (1) 
       ...
       EXECUTE IMMEDIATE "create table t1(c1 int); create table t2(c2 int);" ;
   
   Case (2)
       ...
       LET qry = "select c1,c2 from t1; select c1,c2 from t2";
       PREPARE stmt_id FROM qry;

These examples of EXECUTE IMMEDIATE and PREPARE statements are valid in an
SPL routine:

   Case (3)
       ...
       EXECUTE IMMEDIATE "create table t1(c1 int);" ;
       EXECUTE IMMEDIATE "create table t2(c2 int);" ;

   Case (4)
       ...
       LET qry1 = "select c1,c2 from t1 ;";
       PREPARE stmt_id1 FROM qry1;

       LET qry2 = "select c1,c2 from t2 ;";
       PREPARE stmt_id2 FROM qry2;
   
Only ESQL/C routines can use the EXECUTE IMMEDIATE or PREPARE statement to
process a semicolon-separated list of SQL statements.

-26059	Unsupported data type in EXECUTE IMMEDIATE or PREPARE statement

A variable passed to the EXECUTE IMMEDIATE statement or PREPARE statement 
in SPL must be one of the data types CHAR, VARCHAR, LVARCHAR, NCHAR, or 
NVARCHAR. Change your program logic to pass a variable of one of these 
supported data types to the EXECUTE IMMEDIATE statement or PREPARE 
statement in the SPL routine. 

In an SPL routine, the following examples are not valid because the
EXECUTE IMMEDIATE or PREPARE statement does not support variables of 
type TEXT: 

    Case (1)
        DEFINE qstr REFERENCES TEXT;
        ...
        LET qstr = (SELECT textcol FROM t1 WHERE qryid = 100);
        EXECUTE IMMEDIATE qstr; 

    Case (2)
        DEFINE qstr REFERENCES TEXT;
        ...
        LET qstr = (SELECT textcol FROM t1 WHERE qryid = 100);
        PREPARE stmt_id FROM qstr; 

The following examples include a valid EXECUTE IMMEDIATE statement and a
PREPARE statement in SPL routine:

    Case (3)
        DEFINE qstr varchar(120);
        ...
        LET qstr = "create table t1 (c1 int);";
        EXECUTE IMMEDIATE qstr;

    Case (4)
        DEFINE qstr lvarchar;
        ...
        LET qstr = "select lvarcol from t1 where qryid = 100";
        PREPARE stmt_id FROM qstr;

-26060	Procedure was created with an older version of the engine and must
be dropped and created again in order to work properly.

This procedure was created with an older version of the engine and
contains statements like CREATE TRIGGER, CREATE TABLE with fragmentation
expressions, ALTER TABLE or CREATE TABLE with constraint expressions
or nested CREATE PROCEDURE statements. Such procedures must be dropped
and created again using the current engine.

-26061	Procedure (%s) was created with an older version of the server and must
be dropped and recreated again in order to work properly.

This procedure was created with an older version of the server and
contains statements similar to: CREATE TRIGGER statements, CREATE TABLE 
statements with fragmentation expressions, ALTER TABLE or CREATE TABLE with 
constraint expressions or nested CREATE PROCEDURE statements. These types of 
procedures must be dropped and recreated again using the current server.

-26062	The specified case is not defined in the CASE statement	

The value of the case did not match any value in the WHEN clause of the CASE
statement and no ELSE clause is defined. The following example returns this
error because the LET statement specifies a case that is not defined in the 
CASE statement.

CREATE PROCEDURE case_proc( ) 
RETURNING CHAR(1);
DEFINE grade CHAR(1);
LET grade = 'D';
CASE grade
    WHEN 'A' THEN LET grade = 'a';
    WHEN 'B' THEN LET grade = 'b';
    WHEN 'C' THEN LET grade = 'c';
  END CASE;
RETURN grade;
END PROCEDURE;

EXECUTE PROCEDURE case_proc();

To reduce the risk of this error, you might revise your CASE statement logic to include an ELSE clause to take appropriate action if no WHEN condition is true, or to add additional WHEN clauses that define how to handle additional cases.

-26063	Data type not supported with CASE statement of SPL

The value specified in the CASE statement cannot be of type BLOB, CLOB, BYTE,
TEXT, Collection, DISTINCT, or OPAQUE. The BOOLEAN and LVARCHAR types are the
only built-in opaque data types that the CASE statement in SPL supports.

-26064	The stored procedure execution failed because a statement cannot be prepared(%s).

The stored procedure execution failed because a statement with in the stored
procedure could not be prepared. Change the statement or try to run it with the EXECUTE IMMEDIATE statement. 

-26071	Update statistics is not allowed on cross database or cross server objects

You can only update statistics on objects that reside in the local database.
To update statistics on objects in a remote database, connect to that
database directly.

-26072	The (%s) operator cannot be used in this context.

The word (%s) is an operator in the server and cannot be used in this context.

-11417	Selected locale/codeset will result in performance degradation.

The selected locale/codeset will result in performance degradation.
For optimum performance, use the same client and database locale.

-11418	Locales not found.

The displayed locale and codeset have not been found on the client machine.
Using these locale/codeset will result in -23101 Unable to load locale
categories error message. Please install the locales.

-11419	The connection could not be enlisted with MSDTC.

The connection could not be enlisted with MSDTC because the distributed
transaction has been committed or aborted by the transaction manager. This
problem could be the result of a short transaction timeout in your
application or MSDTC configuration.

-11420	The MSDTC transaction is no longer active.

The MSDTC transaction is no longer active. This problem could be the result
of a short transaction timeout in your application or MSDTC configuration.

-11422	Smart large objects can only be used by setting SQL_INFX_ATTR_LO_AUTOMATIC.

The SQL_INFX_ATTR_LO_AUTOMATIC connection or statement level attribute must be set when working 
with smart large objects. You can set the SQL_INFX_ATTR_LO_AUTOMATIC attribute by setting the 
SQLSetConnectAttr or SQLSetStmtAttr functions or by turning on the Report Standard ODBC Types option 
under the Advanced tab of the ODBC Administration for GBase Driver DSN.

-26073	Savepoint name is unspecified.

The savepoint action - SAVEPOINT or RELEASE SAVEPOINT - requires a savepoint
name to identify the action. Please provide a name for this action.

-26074	Unable to set savepoint %s.

The savepoint referenced in the statement could not be set. Check the
accompanying isam error for more details.

-26075	Unable to release savepoint %s.

The savepoint referenced in the statement could not be released. Check the
accompanying isam error for more details.

-26076	Unable to rollback to savepoint %s.

The transaction could not be rolled back to the savepoint referenced in the
statement. Check the accompanying ISAM error for more details.

-32026	Savepoint not found.

The savepoint referenced in the statement could not be found in the current
transaction. Set a new savepoint using the "SAVEPOINT <savepoint_name>"
statement.

-26077	Savepoint statements are disallowed inside triggers.

Savepoint statements cannot be executed as a trigger action.

-32027	A savepoint with the same name exists and the UNIQUE option was specified.

The savepoint name specified in the SAVEPOINT statement was used in creating 
a previous savepoint, with either the previous, current, or both savepoints 
having the UNIQUE option. After the UNIQUE option is specified, another 
savepoint with the same name cannot be set in the same savepoint level until 
the original savepoint is released. You must either specify a different 
name, or else first release the UNIQUE savepoint before you reuse its 
identifier.

-32028	Limit on savepoint levels reached.

No more than 2 ** 30 of savepoint levels are valid in a transaction. New
savepoint levels are automatically created for the duration of execution of
a stored procedure or UDR. Recursive calls to the stored procedure or UDR
also increment the savepoint level of the current transaction.

-26078	Rollback to savepoint disallowed on updating an old server in same transaction

Rolling back to a savepoint set within a cross-server transaction is not allowed if
a participating server that does not support savepoints is also updated
in the same transaction. End the transaction that updated that server,
and retry the ROLLBACK TO SAVEPOINT statement.

-26079	CONNECT BY query resulted in a loop.

A CONNECT BY query is resulting in a cycle/loop. Use CONNECT_BY_ISCYCLE 
psuedo column to identify the row causing cycle/loop. Change your 
query to remove the row or use the NOCYCLE keyword to allow the query to 
skip the row.

-26080	Generic error in CONNECT BY query processing.

There was a problem in processing the CONNECT BY query. 

-26081	Incorrect use of the CONNECT BY keywords in this context.

The statement could not be run because CONNECT BY clause keywords were used incorrectly.
Keywords that can only be used in the context of a CONNECT BY clause were used outside of a CONNECT BY clause.
One of the following keywords was used outside of the context of a
CONNECT BY clause:
- the LEVEL, CONNECT_BY_ISLEAF, or CONNECT_BY_ISCYCLE pseudocolumn
- the PRIOR or CONNECT_BY_ROOT unary operator
- the SYS_CONNECT_BY_PATH() function
- the ORDER SIBLINGS BY clause
- the START WITH expression clause
Make sure that you have provided CONNECT BY keywords in this context.
Make sure that you have not used nested CONNECT BY clauses in the query.

-26082	CONNECT_BY_ISCYCLE is used without the NOCYCLE keyword.

The CONNECT_BY_ISCYCLE keyword is used without the NOCYCLE keyword 
in a CONNECT BY query. Include the NOCYCLE keyword in the 
CONNECT_BY_ISCYCLE context.

-26084	Cross server objects cannot be referenced in CONNECT BY queries.

The table specified in the FROM clause of a CONNECT BY query must 
be in a database of the Dynamic Server instance to which the session 
is connected. No clause of a SELECT statement that includes the 
CONNECT BY clause can reference a database object that is managed 
by a remore database server.

If you need to perform a CONNECT BY query on a table that resides 
in a database of a remote server, you should first connect to that 
database, and then issue the CONNECT BY query on the (local) table.

-26085	You cannot have a CONNECT BY query on a join of two or more tables.

The FROM clause of a CONNECT BY query cannot have a join of two or more
tables.

For example, the following statements generate an error:

        SELECT empid, name, mgrid, deptdesc
        FROM  (employee INNER JOIN department
               ON employee.deptid == department.deptid)
        START WITH employee.name = 'David'
        CONNECT BY PRIOR employee.empid = employee.mgrid;

        SELECT *
        FROM employee, employee AS new
        CONNECT BY PRIOR employee.empid = new.mgrid;

The following statement is correct:

        SELECT empid, name, mgrid
        FROM  employee
        START WITH employee.name = 'David'
        CONNECT BY PRIOR employee.empid = employee.mgrid;

-26087	Incorrect use of the bson unwind in this context.

The statement could not be run because bson unwind clause was used incorrectly.

-26090	Column (%s) not found in the target table.

This error is generated when a user tries to insert or update data into a 
column that is not from the target table. For example:

        create table bonus (id int, bonus int);
        create table emp(id int, salary int);

        MERGE INTO bonus D
        USING emp S ON D.id = S.id
        WHEN MATCHED THEN UPDATE SET bonus = bonus + salary*.01
        WHEN NOT MATCHED THEN INSERT (id, salary) VALUES (S.id, salary);

This will generate the following error because salary in the insert column
list is not from the target table, bonus:

        26090: Column (salary) not found in the target table.
        Error in line 4
        Near character position 41


        MERGE INTO bonus
        USING emp ON bonus.id = emp.id
        WHEN MATCHED THEN UPDATE SET salary = 1000
        WHEN NOT MATCHED THEN INSERT VALUES (10, 100);

This will generate the following error because salary in the set clause of
the update is not from the target table, bonus:

        26090: Column (salary) not found in the target table.
        Error in line 3
        Near character position 44

-26091	Table (%s) is not the target table.

This error is generated when a user tries to insert data into a column that
is not from the target table. For example:

        create table bonus (id int, bonus int);
        create table emp(id int, salary int);

        MERGE INTO bonus D
        USING emp S ON D.id = S.id
        WHEN MATCHED THEN UPDATE SET bonus = bonus + salary*.01
        WHEN NOT MATCHED THEN INSERT (id, emp.bonus) VALUES (S.id, salary);

This will generate the following error because the emp.bonus table name is 
not the bonus target table:

        26091: Table (emp) is not the target table.
        Error in line 4
        Near character position 47


-26092	Column (%s) is not found in the source table.

This error is generated when a user tries to insert data into the target table
using a column from the target table. For example:

        create table bonus (id int, bonus int);
        create table emp(id int, salary int);

        MERGE INTO bonus D
        USING emp S ON D.id = S.id
        WHEN MATCHED THEN UPDATE SET bonus = bonus + salary*.01
        WHEN NOT MATCHED THEN INSERT (D.id, D.bonus) VALUES (S.id, bonus);

This will generate the following error because in the VALUE clause the
bonus column is not from the emp source table:

        26092: Column (bonus) not found in the source table.
        Error in line 4
        Near character position 66

-26093	Table (%s) is not the source table.

This error is generated when a user tries to insert data into the
target table using the column from the target table. For example:

        create table bonus (id int, bonus int);
        create table emp(id int, salary int);

        MERGE INTO bonus D
        USING emp S ON D.id = S.id
        WHEN MATCHED THEN UPDATE SET bonus = bonus + salary*.01
        WHEN NOT MATCHED THEN INSERT (D.id, D.bonus) VALUES (D.id, salary);

This will generate the following error because the salary column in the VALUE
clause is not from the source table, emp:

        26093: Table (d) is not the source table.
        Error in line 4
        Near character position 61

-26094	The MERGE operation is not allowed on this target table (%s).

The Target table in a MERGE statement cannot be a pseudo SMI table, a catalog 
table, a remote table, a VTI table or an external table.

-26095	Cannot update or delete a row twice in a MERGE statement.

If you do a MERGE operation, the join condition and any filters must be 
sufficiently strict so that no row of the target table is produced more than 
once for updating or deleting. The error occurred because the same row was 
processed twice. The query was terminated abnormally.

Either clean the data source or sources, or make the query more restrictive
so that no target table row is produced more than once for updating or deleting.

-26096	Cannot define INSTEAD OF trigger on a view when the view is specified 
as the target in the MERGE statement.

When the target of a MERGE statement is a view, then INSTEAD OF insert and
update triggers cannot be defined on the view. Delete triggers can be specified
as they are anyway ignored by the MERGE statement.

When this situation occurs, consider merging into the base table directly 
instead of into the view. The other alternative is to drop or disable the 
trigger. 

-26098	In a MERGE statement, the security policies of the target table and source table (%s) do not match.

The Label Based Access Control security polices of the source and target tables in a MERGE statement must be the same.

If the source table object in the USING clause is a subquery that accesses 
multiple tables, you can remove the source table whose policy differs from 
the target table from the USING clause of the MERGE statement.

-26099	The optimizer cannot choose a viable plan based on the ON clause filter
 specified in the MERGE statement.

Hash-join is the preferred join method between source and target tables in 
the MERGE statement. The optimizer currently prevents nested loop joins from 
occurring when the target table is the inner table of a join.  If a hash-join 
plan is also not feasible, then the optimizer throws this error.  

When this error occurs, try to change the ON condition so that at least 
one equality predicate is specified between the source and target tables 
on non-complex data-types. If this is not feasible, please contact 
GBase Technical Support.

-26151	Could not write to external table file: errno, filename.

An error occurred when the database server tried to write to the reject file.

Look for operating-system messages that might give more information.
Possible causes include a full disk or a disk quota limit.

-26152	Could not exclusively lock external table.

Another user is currently using the external table.
Wait for the external table to be unlocked before you proceed.

-26153	Could not close external table.

An error occurred when the database server tried to close the external
table lock. Note all circumstances and contact GBase Technical Support.

-26154	Could not open file: errno, filename.

An error occurred when the database server tried to open the file. Check the
accompanying error for more information. Possible causes include missing
file or incorrect permissions.

-26155	Could not close external table file: errno, filename.

An error occurred when the database server tried to close the file. Look
for operating-system messages that might give more information.
Possible causes include a full disk or hardware errors.

-26156	Failed to read from file: errno, filename.

An error occurred when the database server tried to read from the file.
Check the accompanying error for more information.

-26157	File is incorrectly specified as a DISK type: (file)=(%s).

The file named in the external table was designated as a "DISK" file type,
but it is not a disk file. If this file is a UNIX named pipe, change the 
file type in the external table to type "PIPE".

-26158	File is incorrectly specified as a PIPE type: (file)=(%s).

The file named in the external table was called a "PIPE" file type,
but it is not a UNIX named pipe. If this file is a disk file, change 
the file type in the external table to "DISK".

-26159	Error accessing AIO buffer: errno, filename.

An error occurred when the database server tried to read or write from an
internal AIO buffer. Note all circumstances and contact GBase Software
Support.

-26160	Could not remove the external table file: errno, filename.

An error occurred when the database server tried to remove the
indicated file. Look for operating-system messages that might give
more information. A likely cause is incorrect permissions.

-26161	External table internal error: errno, filename.

Note all circumstances and contact GBase Technical Support
regarding this internal error.

-26162	Failed to start an AIO operation: errno, filename.

An error occurred when the database server tried to read or write to a
datafile. Check the accompanying error for more information.

-26163	Target table cannot have any BLOB columns.

The BLOB column type is not supported for the target column in 
an External table.


-26164	External table data conversion failure (unload).

A conversion failure occurred when the database server tried to convert
the data to ASCII format.

Check that the external table columns have enough space reserved to
write the ASCII representation of the data and that the
columns in the external table are compatible with the data selected.

-26165	Datafile full (unload).

An error occurred when the database server tried to write to the unload
data file. No additional data can be written to the file.

Allocate more space for the file and try again.

-26166	Datafile AIO write error (unload): errno.

An error occurred when the database server tried to write to the unload
datafile. Check the accompanying error for more information.

-26167	All data files are either full or corrupted (unload).

An error occurred during the unload. Look for operating-system messages
that might give more information. Possibly all disks with data files 
are full hardware problems have occurred.

-26168	Conversion error: errno, filename.

When the database server tried to load from the file, it encountered an
error. Unless a reject file is specified in the external table, the
database server returns the error message, and the load job ends without
saving loaded data.

Check for conversion errors, rows that violate constraints defined on
the external table, or null columns that were defined as NOT NULL.

A reason code of UNSUPPORTED_ROW_SIZE indicates that the external table
row size (other than BYTE and TEXT columns) is greater than 32K.

-26169	Failed to access file: errno, filename.

An error occurred when the database server tried to write to the reject file.

Check the accompanying operating-system error indication for more information.

-26170	Could not find record end: must stop loading.

An error occurred when the database server tried to find a record delimiter
in the delimited load file.

Check to see that the file has record delimiters (normally new-line
characters) and that the external table has defined the correct record
delimiter.

-26171	Cannot undo partial write to filename when detecting a full disk.

An error occurred when the database server tried to continue after running
out of space for one data file. When the database server detects that the 
disk is full, it writes to the data file a partial record, which cannot be
truncated.

Look for operating-system messages that might give more information.

-26172	There are too many keywords in USING clause.

The CREATE EXTERNAL table statement contains more than one 
occurrence of a keyword expected in the USING clause.

Review the statement. If you meant to use another keyword, correct the
statement before you reissue it.

-26173	Incorrect value for a keyword.

The CREATE EXTERNAL TABLE statement contains an incorrect value 
for one of the USING clause keywords.

Review the statement to see if the value of the keyword is misspelled.
For the MAXERRORS keyword, make sure the value is a valid number.

-26174	Incorrect DATAFILES entry.

A DATAFILES entry does not have the correct format. The format for an
entry is filetype:rooted-path-name.

Check that the file type is a valid type (DISK or PIPE), that the
second item names a item names a file that can be accessed.  

-26175	DATAFILES entry are missing.

The DATAFILES entry in the CREATE EXTERNAL TABLE statement 
is missing. Add a DATAFILES entry and reissue the statement.

-26176	Cannot use SAMEAS for FIXED format tables.

The CREATE EXTERNAL TABLE statement does not allow the SAMEAS keyword 
for FIXED format tables. The column entries must also define an external 
type to describe how to access the data in the fixed file.

Revise the CREATE EXTERNAL TABLE statement to enumerate the column
information and reissue the statement.

-26178	Incorrect external column type column-name.

The CREATE EXTERNAL TABLE statement contains an incorrect external column
type.

Make sure the external column type is CHAR(size)

-26179	FIXED or DELIMITED columns must be external CHAR type column-name.

The CREATE EXTERNAL TABLE statement contains an external column type
that is not valid. If the table describes a FIXED format file, the valid
external- column type is CHAR. CHAR data must be enclosed in quotation marks.

If the table describes a DELIMITED format file and includes
external-column types, then the type must be CHAR. A table that
describes DELIMITED format files does not need to include
external-column-type information.

-26180	Missing external column type column-name.

The CREATE EXTERNAL TABLE statement is missing the external-column-
type information. This information is required for tables that describe
FIXED format files.

Add the column-type information and resubmit the statement.

-26181	Only FIXED format columns can declare a null column-name.

The CREATE EXTERNAL TABLE table statement has found a column defining a
null string with the NULL "null-string" syntax that is not valid. Only
tables that describe FIXED format files can have a null value defined
with the NULL keyword.

Rework the statement and resubmit it.

-26182	Incorrect file type in DATAFILES string datafile entry.

An error occurred when the database server tried to expand the DATAFILES
entries in an INSERT or SELECT statement for an external table. The
external table has a DATAFILES entry with an incorrect file type. Only
DISK and PIPE file types are allowed for external tables.

Examine the external-table entry, drop the external table, and reissue
a corrected CREATE EXTERNAL TABLE statement to correct this error.

-26183	Could not replace r macro in filename entry.

An error occurred when the database server tried to expand the node
host-name macro in the filename in a DATAFILES entry in an INSERT or
SELECT statement for an external table.

If the error recurs, note all circumstances and contact
GBase Technical Support.

-26184	Could not parse r macro in filename entry.

An error occurred when the database server tried to expand the DATAFILES
entries in an INSERT or SELECT statement for an external table. The
error occurred when the database server tried to expand the %r macro in
the filename. Check that the syntax for the %r() macro is correct.

-26185	None of the DATAFILES strings name valid data files.

An error occurred when the database server tried to expand the
DATAFILES entries in an INSERT or SELECT statement for an external
table. No valid filenames were found in the DATAFILES entries.

Examine the external tables entry, drop the external table, and reissue
a corrected CREATE EXTERNAL TABLE statement.

-26186	File name is too long: file_name.

The name of the file in the external table is too long (after expanding
all the file macros). Check to see that the full path name of the
file is less than 257 characters long.

-26187	Cannot select from multiple external tables.

Only one external table can be used in a SELECT query. For unions,
each UNION contributor can have one external table. External tables
are allowed as participants in joins and subqueries along with
other database tables.

-26188	Null string is too long or has an incorrect format.

The CREATE EXTERNAL TABLE statement has a column with incorrect NULL
"null-string" syntax. The NULL string for this column is longer than
the external column length declared or is not a recognized format for
the external type. 

Check the CREATE EXTERNAL TABLE statement to make sure that the NULL
"null-string" length agrees with the size and format defined for the
external column type.

-26189	Cannot use a <clause> clause with a SELECT statement into an 
external table.

The SELECT statement contains a FOR UPDATE or FORMAT "FIXED" clause. 
When the database server unloads to an external table, these clauses 
are not allowed.

Check the SELECT statement or when the SELECT cursor was defined, and
make sure you do not include either of these clauses.


-26190	Insert into an external table must provide values for all columns in
the table.

The INSERT statement does not list all the columns that the external
table defines. When the database server unloads to an external table, all
columns must have values described by the SELECT list.

Check the INSERT statement to make sure that all columns are included
in the INSERT clause and that every column has a value supplied by the
SELECT list.

-26191	Incorrect use of an external table tablename in query.

This statement describes an operation that is not allowed on an external
table. The types of operations allowed for external tables are
CREATE EXTERNAL TABLE, SELECT, DROP TABLE, GRANT (only SELECT and INSERT), 
REVOKE, CREATE SYNONYM, CREATE SCHEMA, RENAME COLUMN, RENAME TABLE,
SELECT ... INTO EXTERNAL.

The types of operations not allowed for external tables are
TRUNCATE, UPDATE STATISTICS, START VIOLATION, STOP VIOLATION, ALTER TABLE, 
UPDATE, DELETE, CREATE TRIGGER, CREATE INDEX, CONNECT BY, INSERT INTO ... 
VALUES, LBAC operations, target tables of MERGE statements and template
table for SAMEAS clause of CREATE EXTERNAL TABLE.

-26192	Column too long for fixed field.  (Val = value, Col = colno,
Len = converted length, Max = maxLen).

When the database server tried to convert the column to FIXED file format, the
size of the converted value was longer than the maximum size defined
for the fixed- size field.

Check that the external table description defines enough space in the
external column type to hold the data selected after conversion.

-26193	An external table must be a fixed format file if it has an 
external column type column-name.

An external column type of CHAR  was found for an external table that 
is not defined as a FIXED-format file.

-26194	Unknown external column type column-name.

The CREATE EXTERNAL TABLE statement contains an external-column type
that was not recognized. The valid external-column type is CHAR. CHAR 
values must be in quotation marks.

Check the CREATE EXTERNAL TABLE statement to make sure that the
external-column types are valid.

-26195	No constraints can be defined for external tables.

The CREATE EXTERNAL TABLE statement contains one or more types of
constraint that are not allowed.

Check the statement for primary-key, referential, or unique constraints
on a column or for the table.

-26196	Internal type must be a numeric type column-name.

The CREATE EXTERNAL TABLE statement contains external-column types that
do not agree with the internal-column type. 

Check the CREATE EXTERNAL TABLE statement to make sure any external-
column types agree with the internal-column type.

-26197	Reached maximum error limit during load: errno, filename.

When the database server tried to load the data from the external table,
the server reached the MAXERRORS limit defined for the external table.

-26198	Cannot modify an external table that is also used in the subquery.

The external table cannot be used in the subquery. Check the 
statement to see if the external table is used in the subquery.  

-26199	The RETAINUPDATELOCKS session environment cannot be set in a 
nonlogging database.

-26200	The RETAINUPDATELOCKS session environment cannot be set on 
secondary server in a high-availability cluster.

-26213	CREATE EXTERNAL TABLE: The DELIMITER keyword is not valid for
FIXED format tables.

-26214	Cannot perform this operation on an external table.

The following types of operations with external tables are not allowed:
- including an external table in a subquery or in an outer join
- including BLOB or CLOB columns from external tables in joins, UNION
clauses, or ORDER BY clauses
- including a subquery or stored procedure if the main query references
BLOB or CLOB columns in external tables
- using subscripts as BLOB or CLOB columns from external tables
- selecting BLOB or CLOB columns from external tables out of order
- selecting a BLOB or CLOB column from one external table and inserting
it into a different external table
- selecting BYTE and TEXT column to temporary table or through client API

-26216	CREATE EXTERNAL TABLE: The RECORDEND keyword is not valid for
FIXED format tables.

-26381	BLOBDIR directory (%s) does not exist or is not accessible.

The BLOBDIR value specified with the CREATE EXTERNAL or SELECT INTO EXTERNAL 
statement does not exist or is not accessible

-26382	CLOBDIR directory (%s) does not exist or is not accessible.

The CLOBDIR value specified with the CREATE EXTERNAL or SELECT INTO EXTERNAL 
statement does not exist or is not accessible

-26383	DATAFILES string (%s) with file type PIPE is not supported with BLOB/CLOB types

The PIPE option is not supported in the DATAFILES clause when BLOB or CLOB data 
types are specified in the external table definition or in the table referenced in 
the SAMEAS clause. The following is an example of when this error is returned:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB, col3 CLOB)
USING
  (
    DATAFILES
      (
        "PIPE:/tmp/ext_byte.dat.%r(1..2);
         CLOBDIR:/tmp/clobdir1.%r(1..2);
         BLOBDIR=/tmp/blobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

To fix the problem in example above, please change the definition 
to use file type DISK instead of PIPE.

-26384	FORMAT (%s) is not supported with BLOB, CLOB, BYTE or TEXT types

The FORMAT type specified is not supported with BLOB, CLOB, BYTE or TEXT types. 
Use DELIMITED FORMAT when specifying BLOB, CLOB, BYTE, TEXT data types.
The following is an example when this error is returned:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB, col3 CLOB, col4 BYTE,
col5 TEXT)
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
         CLOBDIR:/tmp/clobdir1.%r(1..2);
         BLOBDIR=/tmp/blobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
    FORMAT 'FIXED'
);

To fix the problem in example above, please change the definition 
to use FORMAT type DELIMITED instead of type FIXED.

FORMAT cannot be GBASEDBT or FIXED if the external table contains BLOB, CLOB,
BYTE or TEXT columns.


-26385	Could not parse r macro in BLOBDIR %s.

Could not parse the formatting macro r in BLOBDIR string. Refer to the
 example below for the correct usage:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB, col3 CLOB) 
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
	 CLOBDIR:/tmp/clobdir1.%r(1..2);
	 BLOBDIR=/tmp/blobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

-26386	Could not parse r macro in CLOBDIR %s.

Could not parse the formatting macro r in CLOBDIR string. Refer to the
 example below for the correct usage:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB, col3 CLOB) 
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
	 CLOBDIR:/tmp/clobdir1.%r(1..2);
	 BLOBDIR=/tmp/blobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

-26387	Cannot use r macro in BLOBDIR %s without having a matching macro in filename %s.

Could not use the formatting macro r in BLOBDIR string without having a corresponding 
r macro in the DISK filename. 
The following is an example of when this error is returned:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB) 
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat;
	 BLOBDIR=/tmp/blobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

To fix the error remove the formatting macro r completely or change the 
CREATE EXTERNAL TABLE statement to:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB)
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
         BLOBDIR=/tmp/blobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

-26388	Cannot use r macro in CLOBDIR %s without having a matching macro in filename %s.

Could not use the formatting macro r in CLOBDIR string without having a corresponding 
r macro in the DISK filename. The following is an example of when this error is returned:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 CLOB) 
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat;
	 CLOBDIR=/tmp/clobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

To fix the error remove the formatting macro r completely or change the 
CREATE EXTERNAL TABLE statement to:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 CLOB)
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
         CLOBDIR=/tmp/clobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);


-26389	Range for r macro in BLOBDIR %s does not match range in filename %s.

The r macro range in the BLOBDIR string and filename in the DISK string 
must match exactly.
The following is an example of when this error is returned:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB) 
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
	 BLOBDIR=/tmp/blobdir1.%r(10..20)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

To fix the error change the CREATE EXTERNAL TABLE statement to:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 BLOB)
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
         BLOBDIR=/tmp/blobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

-26390	Range for r macro in CLOBDIR %s does not match range in filename %s.

The r macro range in the CLOBDIR string and the file name in the DISK string
 must match exactly.
The following is an example of when this error is returned:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 CLOB) 
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
	 CLOBDIR=/tmp/clobdir1.%r(10..20)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

To fix the error change the CREATE EXTERNAL TABLE statement to:

CREATE EXTERNAL TABLE exttab (col1 INTEGER, col2 CLOB)
USING
  (
    DATAFILES
      (
        "DISK:/tmp/ext_byte.dat.%r(1..2);
         CLOBDIR=/tmp/clobdir1.%r(1..2)"
      ),
    DELUXE,
    DELIMITER "%",
    recordend "\n",
    maxerrors 20,
    rejectfile "/tmp/ext_byte.log"
);

-26391	FORMAT (%s) is not supported with collection types.

The FORMAT type specified is not supported with collection types.
Use DELIMITED or FIXED FORMAT when specifying collection data types.

-26392	The INSERT operation into an external table failed because a row size exceeds the maximum limit of %s.

During an unload to an external table, the row size exceeded the maximum limit
for external tables.

ACTION

Collection data types or variable length user-defined types in the table can cause the row size
to exceed the external table limit. Large object columns are not included in the row size.
Use another method to unload and load the table, such as the High-Performance Loader,
the UNLOAD and LOAD statements in the DB-Access utility, or the onunload and onload utilities.

-26431	[Internal] Extended type is not valid.

If you cannot find a direct cause for this internal error, note
all circumstances and contact GBase Technical Support.

-26600	Exceeded limit on maximum number of concurrent open databases.

The query is requesting too many concurrent open databases. The query is
likely to have nested routine calls or cascading trigger actions.  Reduce the
nested routine calls or the cascading trigger actions and retry the query.

-26601	Cannot define a partial-column index on the non-character column, column-name.

You can specify a substring size in the CREATE INDEX statement only for
columns of character data types, such as CHAR, LVARCHAR, NCHAR, NVARCHAR,
or VARCHAR.
For the specified column, the index key must be the entire column value.

Remove the substring size value that follows the column name and rerun
the statement.

-26736	The statement failed because the escape character is not a constant of type CHARACTER.

When you use the LIKE or MATCHES operator, the character in the ESCAPE clause 
must be a constant of type CHARACTER.

-43026	The new chunk path <chunk-path> does not exist. User interaction failed
while prompting user for input to continue further.


-43027	The new chunk path <chunk-path> does not exist. User chose to discontinue 
the rename chunk operation when prompted for input.


-43028	The new chunk path <chunk-path> does not exist. User chose to continue the
rename chunk operation without creating the chunk path when prompted
for input.


-43063	The IFX_SERVERNUM environment variable is set to <variable-value>. Using this value
to get logical logs from storage manager for imported restore.


-43064	The IFX_SERVERNUM environment variable is set to an invalid value of <variable-value>.
Using value <variable-value> specified by SERVERNUM parameter in the ONCONFIG file to get
logical logs from storage manager for imported restore.

The IFX_SERVERNUM environment variable set for imported restore is invalid.

ACTION
Set the IFX_SERVERNUM environment variable to a valid value.


-43065	If doing an imported restore, and if the value of SERVERNUM parameter in
ONCONFIG file of the current database server is different from that of the
source database server, try setting IFX_SERVERNUM environment variable
to the source SERVERNUM value prior to re-starting log restore.

The value of SERVERNUM parameter in ONCONFIG file of the current database 
server may be different from that of the source database server.

ACTION
Try setting IFX_SERVERNUM environment variable to the same value as that
of the value of SERVERNUM parameter in ONCONFIG file of the source database
server prior to re-starting log restore.


-43195	ERROR: Logical Logs will not be backed up / salvaged because
LTAPEDEV value is <null-device> . Normal backup (onbar -b) cannot be done
without logical logs being backed up, because restore of such backups
requires a logical log restore.

If the LTAPEDEV parameter value in your ONCONFIG file is /dev/null (UNIX)
or NUL (NT) or blank, logical log backup is not performed.  These are
special values that the user sets to tell the server and ON-Bar that
log backups are not desired.  Data in the logical log cannot be restored.
Normal backups (that are not whole system backups) cannot be restored 
without logical log restore. Therefore such backups are not possible 
without logical logs being backed up.

ACTION
Set the LTAPEDEV parameter value to something other than /dev/null (UNIX)
or NUL (NT) or blank if logical log backups are desired. Or use whole
system backup (onbar -b -w) and whole system physical-only restore
(onbar -r -w -p) which can be performed without logical logs being backed
up / restored.


-43196	ERROR: Logical Logs cannot be restored because LTAPEDEV value is <null-device> .
Restoring a normal backup (done by onbar -b) is not possible without restore
of logical logs. Only whole system backups (done with onbar -b -w) can be 
restored without logical logs as whole system physical-only restore
(using onbar -r -w -p). Then use onmode commands (options -s or -m) to
bring server up without logical log restore.

If the LTAPEDEV parameter value in your ONCONFIG file is /dev/null (UNIX)
or NUL (NT) or blank, logical log restore cannot be performed. These are    
special values that the user sets to tell the server and ON-Bar that
log backup and restore is not desired. Data in the logical log cannot
be restored. 

ACTION
Set the LTAPEDEV parameter value to something other than /dev/null (UNIX)
or NUL (NT) or blank if logical log backup and restore is desired.
Only whole system backups (done with onbar -b -w) can be restored
without logical logs as whole system physical-only restore (using
onbar -r -w -p).


-43197	Duplicate log unique ID <log-uniq-id> found for specified point-in-log restore. 
Onbar is restoring to the latest log unique ID <log-uniq-id>.

Onbar found more than one of the specified point-in-log log unique IDs.
This situation can occur due to multiple timelines. Onbar will restore to 
the latest timeline. 

ACTION
Onbar will restore to the latest timeline. Please use point-in-time restore
to restore to other timelines.


-43250	The child process for the backup and restore filter is terminating 
with exit code <exit-code-number>.

This is an ON-Bar status message. The Child process used during
the backup and restore filter operation is exiting.

ACTION
None


-43298	Warning: BSA version is <xbsa-library-verssion>. This version does not match with the version(s) specified in <sm_versions>.

The version of XBSA shared library could not be identified in sm_versions file. 

ACTION
Look at the contents of the sm_versions file in $GBASEDBTDIR/etc or
%GBASEDBTDIR%\etc.  Make sure the data match the version of your storage
manager, and confirm that this version of the storage manager has been
certified with this version of ON-Bar.


-43299	Error identifying storage manager in XBSA shared library or sm_versions file.

No storage manager could be identified in XBSA shared library or sm_versions 
file.

ACTION
Look at the contents of the sm_versions file in $GBASEDBTDIR/etc or
%GBASEDBTDIR%\etc.  Make sure the data match the version of your storage
manager, and confirm that this version of the storage manager has been
certified with this version of ON-Bar.


-43301	Filter terminated.

This is an ON-Bar status message. The backup and restore filter process 
created by ON-Bar has terminated.

ACTION
None


-43302	Using filter <command-line-to-filter>.

This is an ON-Bar status message. ON-Bar is using this filter program
for backup or restore.

ACTION
None


-43311	Some backups that can be expired were not successfully expired at the Storage Manager.

Onsmsync requested the Storage Manager to expire a backup that is no longer
needed, but the expiration request did not complete successfully.

ACTION
Examine Storage Manager logs and ON-Bar activity log for details. 


-43317	The <COMMAND-LINE-OPTION> feature requires GBase Primary Storage Manager (GBase PSM).

Onsmsync can not complete the request because the Storage Manager does not
support it.

ACTION
To enable this feature, configure ON-Bar to use GBase Primary Storage 
Manager (GBase PSM) as its storage manager.

-43335	Too many onbar command line arguments, maximum allowed: <maimum-number>

This error occurs during read command line arguments.

ACTION
Limit the number of command line arguments to the maximum allowed value.


-43334	(-43334) ERROR: Invalid copy ids received from Storage Manager.

The copy ID that the storage manager returned is inconsistent with the return code

ACTION
Check with the vendor of the Storage Manager.


-43336	(-43336) Logical Logs cannot be backed-up because the database server is dismissing the logical logs.

If the database server was started with an ONCONFIG file containing the LTAPEDEV
parameter with a value of '/dev/null' (UNIX), 'NUL', (Windows) or blank,logical 
log back up cannot be performed. These special values indicate that log backups 
are not desired. Data in this logical log cannot be restored in the future. Only
whole-system backups are allowed.

ACTION
If logical log restores are desired, then set the LTAPEDEV configuration 
parameter value to something other than '/dev/null' (UNIX), 'NUL' (Windows) 
or blank.


-43338	(-43338) Non whole-system backups are not allowed if the database server is dismissing logical logs.

Non whole-system backups need logical logs to be restored to bring the database
server to a consinstent state. If logs cannot be backed up, non whole-system 
backups are not allowed.

ACTION
Set the LTAPEDEV parameter value in the ONCONFIG file to something other than 
'/dev/null' (UNIX), 'NUL' (Windows), or blank and restart the database server.


-43341	(-43341) Logical log <log-uniq-id> is needed by this restore but cannot be found in  (null).

After physical restore of a dbspace, a minimum set of logs can be required to 
maintain the logical consistency of the database server.

ACTION
If this is a whole-system restore, the logical restore part will be disabled
so the DBA can find the logs (if needed) and perform a logical restore (-r -l).

If this is a non whole-system restore, the restore cannot proceed until the
missing log are found.


-43342	(-43342) Cannot read reserved pages from rootdbs object.

The ON-Bar process tried to extract the reserved pages from a rootdbs backup but
was unsuccessful.

ACTION
Call GBase Technical Support.


-43343	(-43343) No suitable root dbspace backup was found.

The ON-Bar boot file (ixbar) does not contain a backup for the rootdbs that can 
be restored with the selected options.

ACTION
If you received this message after running the onbar -r -w command, check if your last backup was a whole backup performed with the onbar -b -w command. You can only do a whole restore on a whole backup. If your last backup was not a whole backup, use the onbar -r command to perform the restore. If your last backup was a whole restore, the ixbar file might be inconsistent. Contact GBase Technical Support.

If you received this message after running the onbar -r command, the ixbar file might be inconsistent. Contact GBase Technical Support.


-43344	(-43344) The rootdbs level 0 restore is not restartable in recovery mode. Shutdown the server and do a cold restore.

The rootdbs level 0 restore must be a cold restore.

ACTION
Shutdown the server and do a cold restore.


-43353	WARNING: Logical logs will not be backed up as part of this operation.
Make sure that logical logs are backed up separately.

The ON-Bar settings switched off implicit log backup after a dbspace backup.

ACTION
Make sure that logs are backed up with a separate ON-Bar command.


-43354	WARNING: Logical logs were not backed up as part of this operation.
Logs through log unique ID %1 are needed for restoring this backup.
Make sure these logs are backed up separately.

The ON-Bar settings switched off implicit log backup after a dbspace backup.

ACTION
Make sure that logs are backed up with a separate ON-Bar command.


-43355	ERROR: Logical logs are full and must be backed up before dbspace
backup without log backup is possible.

The ON-Bar settings switched off implicit log backup.

ACTION
Backup the logs with a separate ON-Bar command.


-43372	Failed to add selected row to linked list for %1.

Attempt to add the selected row to the linked list failed.

ACTION
Stop ON-Bar and retry your command.

If the error persists, please note all circumstances, save a copy of
the ON-Bar and ONLine message logs and contact Technical Support at
support@gbase.cn.


-43374	%1 for %2 exceeds it's maximum allowed length of %3 characters.

SQL statement is longer than it's maximum allowed size.

ACTION
Shorten the statement or divide it into multiple statements.


-43375	ERROR: Attempt to build an SQL where clause for %1 failed.

No data was passed to the build-where-clause function, so no SQL where clause
can be built.

ACTION
Make sure that bar_object and bar_server tables in sysutils
database has data in it.


-43377	%1 required to insert a row into %2.0

An SQL insert into this table can't happen without the specified data.

ACTION
Stop ON-Bar and retry your command.

If the error persists, please note all circumstances, save a copy of
the ON-Bar and ONLine message logs and contact Technical Support at
support@gbase.cn.


-43378	Missing data for %1.

Required data is missing.

ACTION
Stop ON-Bar and retry your command.

If the error persists, please note all circumstances, save a copy of
the ON-Bar and ONLine message logs and contact Technical Support at
support@gbase.cn.


-43379	Updates to <table-name> primary key are not allowed.

Updating a table's primary key is not allowed.

ACTION
First, delete the row and then attempt to insert a new row with the 
new primary key.


-43383	Unable to update timestamp to <timestamp> for action number <action-number>

None

ACTION
Ask your database administrator to repair the data.


-43384	Creating %1 to allow restore %2

missing chunks

ACTION
Creating missing chunks


-43386	Storage space <name> was not backed up so it cannot be restored/verified.

None

ACTION
None


-43387	Unable to determine if <name> is a dbspace or dbslice: %2

An error occurred while trying to convert a dbslice to its list
of dbspaces.  Most likely there is a typographical error or a 
coserver failed to respond.  

ACTION
Verify that all coservers are in the correct state (on-line or quiescent
for a backup or warm restore and micro-kernel for cold restore).


-43396	Logstream <logstream> does not exist.

ACTION
Verify the logstream exists in this database server.


-43397	Storage space <name> does not exist.

ACTION
Verify the storage space exists in this database server and retry the 
backup or restore.


-83336	The warm restore failed because the dbspace number <dbspace_number> is full.

A warm restore operation requires space in a temporary dbspace to write 
the temporary logical logs to be restored. The specified dbspace is now full and 
the warm restore cannot continue.

ACTION
Free space in the dbspace or specify another dbspace and restart the restore.


-26097	Operation is not valid on a secondary server.

The above operation is not valid on the secondary server. 
Attempt such operations only on the primary node in the HDR pair or cluster.


-43083	System call stat() or fstat() failed on file <file name>, error number <OS errno>.

An operating system error prevented information from being returned for the specified the file.

ACTION
Correct the error and retry your command.


-43084	Error <OS errno> while writing data to file <file name>.

An error occurred while writing to the file. The file system might be running out of space.

ACTION
Contact GBase Technical Support.


-83380	An archive checkpoint could not be completed in the secondary server.

An archive checkpoint was attempted on a secondary server but we failed. Possible
causes include the LOG_STAGING_DIR configuration parameter not being set, or not 
receiving a checkpoint from the primary server within the time interval specified
by the BAR_CKPSEC_TIMEOUT configuration parameter

ACTION
Check the online message log for more information and correct the problem.
If the checkpoint timed out you can increase the value of the BAR_CKPTSEC_TIMEOUT
configuration parameter.

-83353	Attempt to reopen a parallel whole system backup failed with return code <error code>..

This is an internal error.

ACTION
Retry the parallel whole system backup. If it fails again, contact GBase Technical Support.

-43141	The edition of GBase Server currently running restricts the number of parallel backup or restore processes to <value>
Resetting BAR_MAX_BACKUP to <MAX VALUE>.

Some editions of GBase Server limit the number of parallel 
backup or restore processes and this limit has been exceeded.

ACTION
The BAR_MAX_BACKUP configuration parameter has been dynamically reset 
to the maximum number processes that are allowed for this edition.

-83974	This edition of GBase Server supports only <Number of Nodes> nodes in a cluster. Stopping connection attempt.

Some editions of GBase Server limit the number of nodes in a cluster.

ACTION
Keep the number of nodes under the allowed value.
Ensure that the number of nodes in the cluster does not exceed the maximum number of nodes supported by the edition.

-83975	This edition of GBase Server does not support <server type>  nodes in a cluster. Stopping connection attempt.

Some editions of GBase Server limit the type of secondary nodes that can be added to the cluster.

ACTION
Avoid adding non-allowed node types to the cluster.


-26401	Cannot connect to accelerator server.

Connecting to the accelerator server failed.
Possible reason can be:
- The accelerator server is not running.
- The connectivity information (a group entry in the sqlhosts file with the
  name of the accelerator) is outdated or invalid.
- The database contains stale meta information (AQTs) of data marts that
  no longer exist.

ACTION
Check if the accelerator server is operational.
Log on to the computer as either user root or as user gbasedbt, and then run
this command: ondwa status

Check the sqlhosts file to find the group entry with the name of the
accelerator. If the entry is considered correct, it is possible that the
authentication token is outdated. It may be necessary to renew the entry
by removing the accelerator and setting up the initial connection anew, as
described in the manual. If the accelerator is shared with another GBase
server instance (e.g. in a HA environment), it may be necessary to get an
up-to-date copy of the sqlhosts entry from one of the sharing GBase server
instances.

The meta information for data marts (AQTs) are special views in the system
catalog table systables of a database, with a name beginning with "aqt".
After removing data marts when connected to a different database, or after
setting up the accelerator server from scratch, it is possible that stale
AQTs are left over. Stale AQTs should be removed manually using the
"DROP VIEW ..." command.


-26402	The dynamic SQL operation failed to run on the accelerator server.

The dynamic SQL operation failed on the accelerator server during the
PREPARE, OPEN, FETCH, or CLOSE phase.

ACTION
Note all the circumstances including the complete error message and
contact GBase Technical Support.

-26403	An SQDWA error occurred.

The query cannot be accelerated due to an internal error that was detected
in the SQDWA component.

ACTION
Note all the circumstances including the complete error message and contact GBase Technical Support.

-26404	The query cannot be accelerated. Fallback to running the query on the database server is disabled.

Is is not possible to accelerate the query and the query cannot be sent to
the database server for processing.

ACTION
To enable the query to run on the database server, run the following
statement and then run the query again: set environment use_dwa 'fallback on'

-26406	Opening multiple cursors to an accelerator server is not allowed.

Opening multiple cursors to an accelerator server from a single session
is not allowed.

ACTION
Close the previous cursor before opening a new cursor to the same accelerator
server.

-26407	Changing the data type for the host variables is not allowed in an accelerated query.

Changing the data type of host variables is not allowed during query
acceleration.

ACTION
Prepare the SQL statement again using the new host variables, and then run the
query again.

-26408	Arithmetic operation resulted in an overflow

The value is too large, and an overflow has occurred.

ACTION
You either sent an arithmetic expression in an SQL statement or prepared
statement to a database server, or a database server returned an arithmetic
expression. This expression caused an overflow. Change the arithmetic
expression so that it does not cause an overflow.

-21528	Defragment: The partition does not require defragmentation.

ACTION
No action required. 

-21529	Defragment: Could not find a large enough extent to cover 2 or more extents.

ACTION
Add another chunk to the dbspace or delete some partitions to create more free space.

-21530	Defragment: Defragmentation is not supported on secondary servers.

ACTION
Defragment on the primary server. Changes will be replicated to the 
secondary servers. 

-21531	Defragment: A partition that contains a partition header page table cannot be defragmented. 

ACTION
Do not attempt to run the defragmenter on the partition header page table - this
 is not allowed.

-21532	Defragment: The partition is in the wrong state or an incompatible type

ACTION
If a conflicting action is occurring on the partition, run defragmentation 
later. Otherwise the partition is a type that cannot be defragmented. See the 
GBase Database Server Administrator's Reference for information on 
defragmentation restrictions. 

-21533	Defragment: A defragmentation task is already in progress on this dbspace.

ACTION
Run one defragmentation task on a dbspace at a time. 

-21534	Defragment: Internal error - cannot map the logical page number.

ACTION
Contact GBase Technical Support. 

-21535	Defragment: Internal error - cannot free the old chunk extent.

ACTION
Contact GBase Technical Support.

-21536	Defragment: Internal error - cannot have two destination extents.

ACTION
Contact GBase Technical Support.

-21537	Defragment: This command cannot be run while a defragmentation is in progress.

ACTION
Rerun the command after defragmentation is complete.

-21539	Defragment: You can't run this command on catalog/pseudo/temp tables

ACTION
Run defragment on a regular table/partition


-26451	STATCHANGE can take values in the range of 0 to 100.

ACTION
STATCHANGE specifies a percentage of how much a table distribution can change before it is considered stale. Set STATCHANGE to an integer value in the range from 0 to 100.

-26452	You cannot specify the STATLEVEL as FRAGMENT for non-fragmented tables.

ACTION
You can set the STATLEVEL attribute to the FRAGMENT option only for fragmented tables. Specify the TABLE or AUTO options for non-fragmented tables.

-26454	Cannot specify both a PRIMARY KEY constraint and a NULL constraint for the same column.

The database server issues this error when the CREATE TABLE or ALTER TABLE  
statement attempts to define a table schema that includes a column definition  
with contradictory specifications regarding NULL values. 
   - The PRIMARY KEY constraint prevents the database server from storing NULL  
     values in this column.
   - The NULL constraint allows the database server store NULL values in this
     column.

To avoid this error, revise the column definition so that it does not include
both PRIMARY KEY and NULL specifications on the same column.
   - If you want this column to be the primary key of this table, you must drop
     the NULL specification in the column definition.
   - If you want to allow NULL values in this column, you must drop the PRIMARY
     KEY specification in the column definition.

-26455	Cannot specify both a NOT NULL constraint and a NULL constraint for the same column.

The database server issues this error when the CREATE TABLE or ALTER TABLE
statement attempts to define a table schema that includes a column definition
with contradictory specifications regarding NULL values.
   - The NOT NULL constraint prevents the database server from storing NULL
     values in this column.
   - The NULL constraint allows the database server store NULL values in this
     column.
 
To avoid this error, revise the column definition so that it does not include
both NOT NULL and NULL specifications on the same column.
   - If you want to prohibit NULL values in this column, you must drop the
     NULL specification in the column definition.
   - If you want to allow NULL values in this column, you must drop the NOT
     NULL specification in the column definition.

-26456	The authorization ID <%s> is not defined for the trusted context.  

Authorization name <authorization-name>  attempted to reuse a trusted connection
using trusted context. The trusted context name can not be used under the
<authorization-name>. The switch user failed. 

This error can also occur for one of the following reasons:

    - The authorization ID is allowed to use the trusted context, but 
      authentication is required and the request to switch users did not
      include the authentication token
    - The authorization ID is allowed to use the trusted context, but the
      trusted context is disabled
    - The system authorization ID attribute of the trusted context object
      associated with the trusted connection has been changed
    - The trusted context object associated with the trusted connection
      has been dropped

The attempt to reuse the trusted connection fails. The trusted connection is 
in an unconnected state.

-26457	The trusted context <context-name> already exists.

The context name is already defined in the database. For CREATE TRUSTED CONTEXT 
or ALTER TRUSTED CONTEXT statement, a trusted context with the specified name
already exists. Verify that you have specified the correct context name and that 
you are in the correct database. 

-26458	The trusted context specified authorization ID <authorization-name>
which is already specified for another trusted context.

A CREATE TRUSTED CONTEXT or ALTER TRUSTED CONTEXT statement for specified
SYSTEM AUTHID <authorization-name>, but this authorization ID is already
defined to use a different trusted context. A system authorization ID
that is defined as the SYSTEM AUTHID for a trusted context cannot be 
associated with any other trusted context as the SYSTEM AUTHID.

Use the following query to determine which trusted context is already
using the authorization ID:

SELECT CONTEXTNAME FROM SYSUSER.SYSTRUSTEDCONTEXT
WHERE AUTHID = <authorization-name>

To correct this error change the authorization ID for the trusted context
and reissue the CREATE or ALTER statement.

-26459	The trusted context <context-name> does not exist.

The specified trusted context <context-name> does not exist in the database.
Verify that you have specified the correct trusted context name. 

-26460	Attribute with value <value-name> cannot be dropped or altered
because it is not part of the definition of trusted context.

Attribute <value-name> was specified for a trusted context, but the 
trusted context is not defined with an attribute with this name. The statement
could not be processed.

To correct this error remove the name of the unsupported attribute and 
re-issue the statement.

-26461	Attribute with value <value-name> is not unique for trusted context. 

During the CREATE TRUSTED CONTEXT or ALTER TRUSTED CONTEXT a duplicate 
value <value> was specified for the attribute. Each pair of attribute name
and value must be unique for a trusted context. The statement cannot be processed.

To correct this error remove the non-unique specification of <attributename> 
and and re-issue the statement.

-26462	User <user-name> cannot be dropped or altered because it is not part 
of the definition of trusted context 

User <user-name> was specified for a trusted context, but the 
trusted context is not defined with an user with this name. The statement 
could not be processed.

To correct this error remove the name of the undefined user and 
re-issue the statement.

-26463	User <user-name> is not unique for trusted context.

During the CREATE TRUSTED CONTEXT or ALTER TRUSTED CONTEXT a duplicate 
value of  user <user-name> was specified in the WITH USE FOR clause.
The statement cannot be processed.

To correct this error remove the non-unique specification of user name 
and and re-issue the statement.

-26464	A CREATE TRUSTED CONTEXT or ALTER TRUSTED CONTEXT statement specified 
<%s> more than once or the trusted context is already defined to be used by
this authorization ID or PUBLIC.

The statement specified that <authorization-name> be allowed to use the trusted
context, but the specified authorization ID or PUBLIC is already defined to use 
the trusted context, or the authorization ID was specified more than once in 
the statement. The authorization ID or PUBLIC must not already be allowed to use
the trusted context, and it can only be specified once within a statement for a 
trusted context. The statement could not be processed.

If the authorization ID or PUBLIC was specified more than once, remove the extra
specifications of <authorization-name>, and re-issue the statement. If an ALTER 
TRUSTED CONTEXT statement that contained an ADD USE FOR clause, and the trusted
context already was defined for use by that authorization ID or PUBLIC, use the
REPLACE USE FOR clause instead to redefine the usage characteristics for the 
specified users to use the trusted context.

-26465	An ALTER TRUSTED CONTEXT statement for specified <%s> but the trusted 
context is not currently defined to be used by this authorization ID or PUBLIC.

An ALTER TRUSTED CONTEXT statement for attempted to replace or remove the
ability for <authorizationname> to use the trusted context, but the specified
authorization ID or PUBLIC is not currently defined to use the trusted context.
The statement could not be processed.

If an ALTER TRUSTED CONTEXT statement that contained a REPLACE USE FOR clause, 
and the trusted context was not already was defined for use by that authorization 
ID or PUBLIC, use the ADD USE FOR clause instead to define the trusted context to
be used by the specified users. If the ALTER TRUSTED CONTEXT statement contained 
the DROP USE FOR clause, none of the specified authorization IDs or PUBLIC were 
currently defined to use the trusted context.

-26466	ENCRYPTION attribute <%s> cannot be specified more than once.

Specify only one ENCRYPTION attribute in a statement. Alternatively, specify one
WITH ENCRYPTION attribute for each ADDRESS attribute. 

-26468	A DBSECADM users cannot create a trusted context for themselves.

The statement (CREATE TRUSTED CONTEXT or ALTER TRUSTED CONTEXT) was not
processed because the specified SYSTEM AUTHID matched that of the DBSECADM
user who ran the statement.

Specify a different authorization ID or run the statement as a different 
DBSECADM user.

-26469	A trusted connection was not established because the trusted context 
is not enabled. 

Ensure that the trusted context is correctly defined and enabled, and then try
to establish a trusted connection.

-26470	The database specified ('database name') is not associated with the
trusted context.

Specify the correct database for the trusted context associated with
the current trusted connection or establish a regular connection to the database.

-26471	Internal Function (%s): Unable to send or receive from the Session Manager.

This error indicates that the server was able to establish connection to the
Session Manager but failed to send or receive data.
If you cannot find a direct cause for this internal error, note
all circumstances and contact GBase Technical Support.

-26472	Internal Error occurred during a BLOB operation in function %s.

If you cannot find a direct cause for this internal error, note
all circumstances and contact GBase Technical Support.

-26473	Internal Error occurred. The required parameter %s is NULL.

If you cannot find a direct cause for this internal error, note
all circumstances and contact GBase Technical Support.

-26474	Internal Error occurred during codeset conversion in function %s.

If you cannot find a direct cause for this internal error, note
all circumstances and contact GBase Technical Support.

-26475	Function (%s): Unable to connect to the Session Manager.

This error indicates that the server is unable to connect to the Session Manager
which is necessary for debugging the SPL routine. Verify that the Session Manager
is running in the specified host system at the specified port number. Also make
sure that the Session Manager host and the port can be accessed from the host
system where the server is running.

-26476	Can not create temp table with ERKEY

It is illegal to create a temp table with ERKEY

-26477	Illegal usage of ERKEY

Your usage of ERKEY is not legal

-26478	Can not add ERKEY when table already has ERKEY

You attempted to alter a table to include the ERKEY attribute when that table
already had the ERKEY attribute.

-26479	Can not drop ERKEY when table does not have ERKEY

You attempted to alter a table to drop the ERKEY when the table did not
have the ERKEY attribute.

-26480	Illegal usage of ifx_replcheck

You attempted to use the ifx_replcheck column in an illegal manner.

-26481	Cannot perform this operation through a grid

Renaming replicated database is not supported through a grid.
Manually rename the replicated database at all grid servers outside
of the grid context.

-26482	Cannot alter a replicated table in a grid outside of the same grid context

The ALTER TABLE statement on a replicated table that was created through 
a grid must be executed within the same grid context and with 
replication enabled by setting the ER_enable argument to 1.

-26483	Grid or Region is not defined

A SET ENVIRONMENT GRID_SELECT command was issued using a non-existant
grid or region name.

-26484	SQL error encountered on a SET ENVIRONMENT GRID_SELECT command

Examine the message log file for more detailed information.

-26491	JDBC method (%s) not supported with this server.

A Java UDR invoked a method from the JDBC API that is not supported 
by J/Foundation in this version of the server. See the GBase release
notes for more information about JDBC compliance.

-26500	Query offloading is turned OFF

This query was not accelerated because query offloading is turned off.
To turn on query offloading, use the following statement:
SET ENVIRONMENT use_dwa '1'

-26501	Subquery matching is not supported

This subquery was not accelerated because accelerating a subquery is not
supported yet.

-26502	Contradictory filters in where clause will not produce any rows

This query was not accelerated because the where clause have contradictory
filters which will result in no rows. Hence there is no need to off-load this
query to accelerator.

-26503	Statement is not a SELECT or SELECT INTO statement

This statement was not accelerated because it is not a SELECT or
INSERT INTO ... SELECT ...  statement.

-26504	Query containing FOR UPDATE is unsupported for offloading

This query was not accelerated, because it contains a FOR UPDATE clause.

-26505	Query contains a pseudo table

This query was not accelerated, because it contains a pseudo table.

-26506	Query contains a temporary table

This query was not accelerated, because it contains a temporary table.

-26507	Query contains a table which is not a real table

This query was not accelerated, because it contains a table which is not a real
table (e.g. view, external table, sequence, synonym).

-26508	Query contains a system catalog table

This query was not accelerated, because it contains a system catalog table.

-26509	Query contains a table in remote database

This query was not accelerated because it contains a table that does not reside
in the database to which the user is connected.

-26510	Cannot identify fact table

This query was not accelerated because the fact table cannot be identified.
For outer joins, the fact table is the leftmost table in the query.
For inner joins, the fact table is the table containing most rows.

-26511	Cannot access the AQT dictionary

This query was not accelerated because an error occurred when trying to access
the AQT dictionary cache.
Note all circumstances and contact GBase Technical Support.

-26512	No data marts are defined on this database
This query was not accelerated because no data marts are defined on the current
database.

-26513	Data mart is virtual but the AVOID_EXECUTE directive is not set

This query was not accelerated because the data mart it matches to is a virtual
data mart. To match against virtual data marts, the AVOID_EXECUTE optimizer
directive or explain setting must be set.

-26514	Query contains more tables than data mart reference definitions

This query was not accelerated because it contains more tables than the number
of references in the data mart definitions. GBase offloads only queries fully
contained in a single data mart.

-26515	Query does not contain a fact table of any data mart

This query was not accelerated because it does not contain a fact table of any
data mart defined on this database.

-26516	Query contains combination of tables not contained in any data mart

This query was not accelerated because it uses a set of tables that is not
contained in any data mart definition. Check the tables and references used in
your data mart definitions.

-26520	ON clause contains a non-equality join between columns

This query was not accelerated because it contains an OUTER join with a
non-equality join predicate between two columns. Query acceleration is only
possible on equality join predicates of two columns.

-26525	OUTER join with in-join filter (extends NULLs)

The query was not accelerated because it contains an OUTER join with
in-join filter.
The ON clause of an ANSI OUTER join query must not contain other expressions
than equality joins between columns (e.g. non-equality filters or table filter).
The WHERE clause of an GBASEDBT OUTER join query must not contain non-equality
filters or table-level filters on subservient tables. 

For example, the following queries cannot be accelerated. 
ANSI outer join queries : 
SELECT * FROM f left join d ON (f.col1 = d.col1 AND f.col2 > d.col2) 
SELECT * FROM f left join d ON (f.col1 = d.col1 AND f.col2 > 0) 
SELECT * FROM f left join d ON (f.col1 = d.col1 AND d.col2 > 0) 
GBase outer join queries : 
SELECT * FROM f outer d WHERE f.col1 = d.col1 AND f.col2 > d.col2 
SELECT * FROM f outer d WHERE f.col1 = d.col1 AND d.col2 > 0 

-26528	OR clause contains joins between different tables

This query was not accelerated because it contains an OR clause with equality
join predicates that refer to more that two tables.

-26529	OR clause contains joins between different columns

This query was not accelerated because it contains an OR clause with equality
join predicates that refer to more that two columns.

-26530	Query contains a full join

This query was not accelerated because it contains a full OUTER join.
Full OUTER joins are not eligible for query acceleration.

-26532	ON clause of LEFT OUTER refers to more than two tables

This query was not accelerated because it contains a LEFT OUTER join with an
ON clause that refers to more than two tables.

-26534	INNER join must be on first level of joins

This query was not accelerated because it contains an INNER join that is not
on the first level of joins. Query acceleration on INNER joins is only possible
if the INNER join in on the first join level.

-26536	INNER join contains a non-equality join between columns

This query was not accelerated because it contains an INNER join with a
non-equality join predicate between two columns. Query acceleration is only
possible on equality join predicates of two columns.

-26537	INNER join refers to more than two tables

This query was not accelerated because it contains an INNER join with an
ON clause that refers to more than two tables.

-26538	Joins do not form a star or snowflake scheme

This query was not accelerated because it does not join the contained tables
in a star or snowflake scheme. The number of joins must be one less than the
number of tables.

-26540	Table cannot be fact table, it is used by query with different aliases

This query was not accelerated because it contains the fact table more than
once. The fact table is used by the query with different aliases.

-26543	Query does not contain an equality join from the data mart definition
	and none of the columns in the equality join has a unique index

This query was not accelerated because it does not contain an equality join that
is present in the data mart definition, and none of the columns in the equality
join has a unique index. If a reference in the data mart definition is not
reflected by the corresponding equality join predicates in the query, then these
equality join predicates can be omitted from the query only if at least one of
the columns has defined a unique index on it. Otherwise, the accelerator does
not preserve the correct number of rows.

-26544	Query does not contain a table from the data mart definition
	and there is no unique index defined on that table

This query was not accelerated because it does not contain a table that is
present in the data mart definition with a reference defined on it, and there
is no unique index defined on that table. If a reference in the data mart
definition contains a table that is not present in the query, this table can be
omitted from the query only if there is a unique index defined on it. Otherwise,
the accelerator does not preserve the correct number of rows.

-26546	Query contains equality joins that are not present in the data mart
	definition

This query was not accelerated because it contains equality joins that are not
defined as references in the data mart definition.

-26547	Query does not contain an equality join from the data mart definition
	and none of the columns in the equality join from the data mart
	definition have a unique index

This query was not accelerated because it does not contain an equality join that
is present in the data mart definition and none of the columns in the equality
join of the data mart definition have a unique index. If a reference in the data
mart definition is n:m, then this reference needs to be reflected by an equality
join in the query.

-26552	Column is not contained in the data mart definition

This query was not accelerated because it refers to a column that is not
contained in the definition of the data mart.

-26555	Expression is not supported

This query was not accelerated because it contains an expression that is not
supported by the accelerator.

-26557	Data type is not supported

This query was not probed successfully because it contains a column whose
data type is not supported by the accelerator.

-26558	Query contains implicit column

This query was not accelerated or probed successfully because it contains
an implicit column. Tables contain implicit columns if they are created or
altered using the following options:

option           | implicit columns
-----------------+--------------------------------------
WITH CRCOLS      | cdrserver, cdrtime
WITH REPLCHECK   | ifx_replcheck
WITH VERCOLS     | ifx_insert_checksum, ifx_row_version
WITH ERKEY       | ifx_erkey_1, ifx_erkey_2, ifx_erkey_3
WITH ROWIDS      | rowid

-26559	Query does not explicitly contain columns

This query was not probed successfully because it does not explicitly contain
columns. E.g. a query like "select count(*) from t" does not select specific
columns.

-26560	Cannot find synonym for table.

This query was not probed successfully because it contains a table that does
not reside in the current database and no synonym is defined on this table.

-26561	Default gbase sqlcode returned for not matched ISAO server sqlcode.

This query returned a error from the accelerator in drda sqlcode and 
drda sqlstate form. This error did not match any of the existing IDS sqlcode.
So IDS server gives this default sqlcode error.

-26563	The query cannot be accelerated because the unary function with expression is not supported on the accelerator.

The query contains unary function with expressions as argument. If the data types in the expression are non-numeric types then the accelerator returns error. So such queries need to be prevented from off-loading to the accelrator.


-26564	The Query containing windowed aggregates is unsupported for offloading

This query was not accelerated, because it contains a windowed aggregates.


-26565	Row type cannot be used as an OUT/INOUT parameter in a C UDR when the
routine is invoked by another SPL routine.

-26700	User (<username>) was not found.

The specified user name is not registered in the database server. This error 
occurred when the ALTER, RENAME, or DROP statement was run. Verify the user name 
and then rerun the statement.  It is also possible that the user was previously 
removed or renamed.

-26701	User (<username>) was not created because it already exists.

Verify the user name. If you want to create a user, specify a unique user name 
that is not registered in the database server. If you want to change the 
properties of a user account, you can use either the DROP USER or RENAME USER 
statement and then rerun the CREATE USER statement. You can also use the ALTER 
USER statement to change the properties of an existing user. 

-26702	User (<username>) cannot connect to the database server because the user account is locked.

To unlock the user account, run the ALTER USER statement with the ACCOUNT UNLOCK 
option. Only a database server administrator (DBSA) can manage user accounts. 
In a non-root installation, the DBSA is the user who installed the product. 
Log in as the user who installed this database server to modify user accounts. 

-26703	User (<username>) is not authorized to create, alter, drop, or rename users.

Only a database server administrator (DBSA) can manage user accounts. In a 
non-root installation, the DBSA is the user who installed the product. Log in as 
the user who installed this database server to modify user accounts.  

-26704	User name (<username>) exceeds the maximum length. Specify a user name that is not longer than 32 characters.

-26705	The password specified for user (<username>) is not valid. Specify a password that contains 6 - 32 characters.

-26706	Cannot add a password to the user (%s) because a password already exists. Use the MODIFY option instead of the ADD option.

An ALTER USER operation can only add a password for a user if that user does not have a 
password. If you want to change the password, use the MODIFY option in the ALTER statement. 
If you want to drop the password, use the DROP option in the ALTER statement.

-26707	User (<username>) cannot be created because the user is not mapped to any properties.

When you create a user for a non-root installation, you must use the PROPERTIES 
option with the CREATE USER statement. You can exclude the PROPERTIES option in 
the CREATE USER statement if the user has operating-system properties on the 
host computer. Also, you can exclude the PROPERTIES option if default properties 
exist on the database server.  

-26708	Incorrect old password supplied for user (<username>).

-26709	The new password specified for user (%s) is not valid. Specify a password that contains 6 - 32 characters.

-26710	PUBLIC is a reserved word. You cannot create, drop, alter, or rename a user with the name PUBLIC.

-26711	The default user was not found.

The default user is not registered in the database server. This error occurred when the ALTER, RENAME, or DROP statement was run. Verify the default user is registered and then rerun the statement.  It is also possible that the default user was previously removed or renamed.

-26712	The default user was not created because it already exists.

If you want to change the properties of the default user, you can use the ALTER DEFAULT USER statement. Alternatively, you can run the DROP DEFAULT USER statement and then rerun the CREATE DEFAULT USER statement with different properties.

-26713	Do not specify a password while creating the default user.

The default user can have properties, but it cannot have a password. User accounts associated with the default user have the same properties.

-26714	An internal error occurred while hashing the password. Record all circumstances prior to the error and contact GBase Technical Support.

-26715	Cannot alter the user (<username>) because only one USER or UID property is allowed.

A user must have either one USER property or one UID property. The ALTER operation failed 
because it would not have resulted in one USER or UID property.

-26716	Cannot alter the user to add groups because the number of groups would exceed the maximum limit (<max_groups>).

The total number of groups after the ALTER USER operation cannot exceed the maximum number 
of allowed groups (16).

-26717	An internal error occurred while performing an ALTER operation. Note all
circumstances and contact GBase Technical Support.

-26718	Cannot alter the user (<username>) to add a home directory because the property value already exists. Use the MODIFY option instead of the ADD option.

An ALTER USER operation can only add a home directory if no home directory exists. If you 
want to modify the home directory, use the MODIFY option in the ALTER statement.

-26719	The ALTER statement specified an incorrect authorization value (<auth-value>).

The valid values for the AUTHORIZATION option are "dbsa", "dbsso", "aao", and "bargroup".

-26720	Cannot change a property value more than once in the same ALTER statement.

In a single ALTER statement a property can only be changed once.

-26721	Cannot drop the password for the user (<username>) because the password is specified. Do not include a value for the PASSWORD property with the DROP option.

When dropping password for the user, the password value cannot be specified.

-26722	The surrogate user name (<username>) exceeds the maximum length of 32 characters. Specify a user name that has 32 characters or fewer.

-26723	The value of the surrogate property HOMEDIR exceeds the maximum length. Specify a value that is less that 255 bytes.

-26724	Cannot drop the HOMEDIR property for the user (<username>) because a value is specified. Do not include a value for the HOMEDIR property with the DROP option.

In an ALTER USER operation to drop a specified property, a value should be supplied only 
when the property is GROUP or AUTHORIZATION. For all other properties, a property value 
cannot be specified.

-26725	Cannot drop the UID property for the user (<username>) because a value is specified. Do not include a value for the UID property with the DROP option.

In an ALTER USER operation to drop a specified property, a value should be supplied only 
when the property is GROUP or AUTHORIZATION. For all other properties, a property value 
cannot be specified.

-26726	Cannot drop the USER property for the user (<username>) because a value is specified. Do not include a value for the USER property with the DROP option.

In an ALTER USER operation to drop a specified property, a value should be supplied only 
when the property is GROUP or AUTHORIZATION. For all other properties, a property value 
cannot be specified.

-26727	The SQL statement cannot assign operating system properties to the user (<username>).

The specified user account is an operating system user account and it is managed by 
the operating system administrator. You can use the CREATE USER and ALTER USER 
statements only to grant administrative database server privileges to this user 
account. Use the AUTHORIZATION keyword with a valid authorization property: DBSA 
(Database Server Administrator), DBSSO (Database System Security Officer), 
AAO (Audit Analysis Officer), or BARGROUP (authority to execute ONBar commands).

-26728	The uid %s is not in the /etc/gbasedbt/allowed.surrogates file or in the cache.

You must add the uid before it can be a surrogate.

-26729	The user %s is not in the /etc/gbasedbt/allowed.surrogates file or in the cache.	
You must add the user before it can be a surrogate.

-26730	The gid %s is not in the /etc/gbasedbt/allowed.surrogates file or in the cache.	

You must add the gid before it can be a surrogate.

-26731	The group %s is not in the /etc/gbasedbt/allowed.surrogates file or in the cache.	
You must add the group before it can be a surrogate.

-26732	The USERMAPPING feature is disabled. The USERMAPPING configuration parameter must be set to BASIC or ADMIN.

-26733	A gridtable can only be altered, renamed or dropped 
within a grid context.
You must Connect to the appropriate grid prior to issueing the alter or 
drop table statement.

-26734	A database containing gridtables can only be droppen within a grid context
You must Connect to the appropriate grid prior to dropping the database

-26735	Invalid parameter for CLUSTER_TXN_SCOPE.  The only allowed values are DEFAULT, SESSION, SERVER, and CLUSTER

-84221	Server name (<GBase Server>) already exists with a different definition in the source's SQLHOSTS file.

The server name specified as target for the snapshot copy operation already exists
in the SQLHOST file of the source server and it points to a different TCP/IP port
and/or machine.

Fix the SQLHOSTS files and retry the operation.

-84502	XA transactions are not supported on read-only secondary servers	
An XA transaction was run on a read-only secondary server.

ACTION
Modify your application to prevent the xa start() function from running on a read-only secondary server. Or, make the secondary server updatable using the UPDATABLE_SECONDARY configuration parameter.

-26801	Cannot reference an external database that is not case sensitive.

This statement refers to a database other than the current database.
However, the current database is a case sensitive database, and the
external one is not. This action is not supported, because the databases
that are used in a single distributed transaction either must all be case 
sensitive, or else must all be case insensitive.

-26802	Cannot reference an external database that is case sensitive.

This statement refers to a database other than the current database.
However, the current database is a case insensitive database, and the
external one is not. This action is not supported, because the  databases
that are used in a single distributed transaction must all be case 
sensitive, or else must all be case insensitive.

-33488	The options specified require a C++ compiler, but one could not be 
found.

The options passed to the ESQL/C compiler require that the application be linked
with a C++ compiler. Ensure that a C++ compiler is installed on your system and
that the PATH environment variable contains the location of your C++ compiler.

-33489	The GL_USEGLU environment variable setting requires a C++ compiler,
but one could not be found.

The GL_USEGLU environment variable setting requires that the application be linked
with a C++ compiler. Ensure that a C++ compiler is installed on your system and
that the PATH environment variable contains the location of your C++ compiler.
If ICU support is not required by your program, unset the variable.

-26901	An alias cannot represent another alias.

The statement failed because the alias definition specified that the alias 
represented another alias instead of a real table name. 
A table can have multiple aliases, but an alias cannot have an alias. 
Rerun the statement with a real table name before the AS keyword in the alias 
definition.

-26902	[Internal] Client decimal buffer size mismatch

A client application has sent the incorrect buffer size for a field that has a decimal or numeric
data type. This can lead to a loss of precision or the truncation of digits of the decimal value.

ACTION
If you are using GBase JDBC driver versions 3.50.JC8, 3.70.JC2, or earlier, upgrade to the 
latest version of the GBase JDBC driver.

If you want to ignore this error, set the database server environment variable
IFMX_TEMP_CQ00225490 to 1 and restart the database server.

-26903	Multiple execution of a CREATE TABLE statement that was prepared once 
is not allowed.

A CREATE TABLE statement that was prepared only one time cannot be executed 
multiple times.

ACTION
Either set the AUTO_REPREPARE configuration parameter to 1 in the onconfig file
or include the FREE statement after the PREPARE CREATE TABLE and EXECUTE 
statements.

-26904	Attached, non-vanilla or interval fragmented compressed indexes are not supported.

Compressed indexes must be detached indexes but not interval fragmented or non-vanilla.

-26905	The procedure was not created because its definition has more than 341
parameters

The maximum number of parameters in a procedure definition is 341. Reduce the
number of parameters to 341 or fewer and retry the CREATE PROCEDURE statement. 

-26907	Routine creation failed because a collection variable was defined as a
global variable.

A variable that is based on a collection data type cannot be declared as a
global variable.

-26950	Clustered compressed indexes are not supported.

Compressed indexes must be non-clustered indexes. After you create a 
clustered index, you can compress it  using an SQL admin API command.

-26951	The grid query failed to run.  Contact GBase Technical Support.

-26952	The statement failed because the grid query contained a server name.

Server names are not valid when a GRID clause is used.  Remove any 
reference to a specific server from the query and run the statement again.

-26953	The statement failed because the grid or region does not exist.

The specifid grid or region is not defined.  Include an existing grid or
region name in the GRID clause and run the statement again.

-26954	The statement failed because the grid query contained a UNION or UNION ALL operator.

Statements that include the GRID clause cannot contain the UNION or UNION ALL operators.  Remove the UNION and UNION ALL operators from the query and run the statement again.

-26955	The statement failed because of an invalid value for the SET ENVIRONMANT GRID_NODE_SKIP statement

The valid values for the GRID_NODE_SKIP option are DEFALULT, 'on', or 'off'.  Run the statement again with one of the valid values.

-26956	Cannot run the SET ENVIRONMENT SELECT_GRID or SELECT_GRID_ALL statement.

There was not enough memory to execute the command.

-26957	The statement failed because the grid query cannot connect to a server.

One of the servers in the grid is not currently available.  Wait until the 
server is available, or run the SET ENVIRONMENT GRID_NODE_SKIP 'on' 
statement and then run the statement again.

-26958	The statement failed because the grid query contained one or more subqueries.

Satatements that include the GRID clause cannot contain subqueries.  Remove all subqueries from the query and run the statement again.

-26959	The statement failed because the syntax for the grid query is incorrect.

Fix the grid query syntax and run the statement again.

-26960	The statement failed because the grid query contained a table that is not a grid table.

Make the table available for a grid query by running the 
cdr change gridtable command or by using OAT.  Then run the statement again.

-26961	The statement failed because a table in the grid query is in the process of being altered.

A table that is being altered cannot be included in a grid query until
the alter operation has propagated to all nodes in the grid.

-26962	The statement failed because the grid query contained coorelated joins.

Statements that include the GRID clause cannot contain correlated joins.   Remove all correlated joins from the query, and run the statement again.

-26963	The statement failed because the grid or region has no members.

Specify a different grid or region in the GRID clause, or add members to the
grid or region, and then run the statement again.

-26991	The query failed because the FROM clause includes more than one sharded table.

A SELECT statement cannot include more than one sharded table in the FROM clause.

-26992	The sharded query failed because of an internal error.

The query on a sharded table encountered an internal error. If this error
recurs, note all circumstances and contact GBase Technical Support.


-26993	Cannot alter the table to shard collection table.

Collection table can only be altered to shard collection table.


-32519	Passwords are only allowed in SET SESSION AUTHORIZATION statements
for a trusted connection using a trusted context.

A SET SESSION AUTHORIZATION statement with a password, was executed outside
of a trusted connection.

-88001	C-ISAM function <FUNTION NAME> returned error <ERROR CODE>. 

The C-ISAM processor returned an error.

Check the C-ISAM error code and correct the situation.

-88002	Memory allocation error.

An attempt to allocate memory from the system failed.

Release some memory and retry the operation.

-88003	Cannot create a file or directory at <PATH>.

GBase PSM attempted to create a file or directory but the attempt failed.

Check the associated operating system error and correct the situation.

-88004	Cannot rename a file or directory to <PATH>.

GBase PSM attempted to rename a file or directory but the attempt failed.

Check the associated operating system error and correct the situation.

-88006	An unspecified error occurred,

A generic, unexpected error happened.

Check additional errors and the configuration and contact GBase Technical Support if the problem persist.

-88008	The specified path <PATH> is not a directory or does not exists.

GBase PSM Attempted to open a directory that does not exist.

Correct the situation and attempt to run the command again.

-88014	When creating a new device, a device type is needed.
When creating a new device, a device type is needed.

Provide a device type (either FILE or TAPE) and retry the operation.

-88015	A block size must be specified for device type %s.

The specified device type requires a block size.

Provide a block size and retry the operation.

-88016	An empty pool was provided.

A valid pool name is needed for the operation.

Provide a valid pool name and retry the operation.

-88017	Invalid pool <POOL-NAME> specified

The specified pool is not known.

Provide a valid value for the pool. 

-88018	Invalid device type <DEVICE-TYPE> specified.

The specified type of device is not known.

Provide a valid value for the device type.

-88019	Invalid block size for device <DEVICE-PATH>.

The block size specified for the device is not correct.

Fix the block size and retry the operation.

-88020	nvalid media size for device <DEVICE-PATH>.

The media size specified for the device is not correct.

Fix the media size and retry the operation.

-88021	Cannot update the device <DEVICE-PATH> because it is in use. 

NONE

NONE

-88022	Error -88022: Cannot delete the device <DEVICE-PATH> because it is in use.

GBase PSM Failed to delete the named device because it is in use.

Wait until the device is idle and retry the operation.

-88023	Error -88023: Cannot open the device definition file <TEXT-FILE>.

Attempt to open a GBase PSM device definition file failed.

Check the associated operating system error code.

-88035	No Device is available to mount in pool <POOL-NAME>.

The storage manager was asked to mount a volume for a specified 
pool, but no device was found.

Be sure that a device has been created for the specified pool.
Also be sure that the devices for the given pool are available.

-88056	The device requested in the operation (<OPERATION>) does not exist.

You tried to perform an operation on a device that does not exist. For example you tried to delete a device that does not exist.

Resubmit the command with the correct device or pool.

-88057	The specified device does not belong to the specified pool.

Either the device or the pool is incorrect

Correct the device or pool provided.

-88058	The specified device <DEVICE-PATH> cannot be deleted.

An error prevented the storage manager from deleting the specified device.

Check the accompanying error and fix the situation.

-88101	Cannot create catalog tables

Check the GBase PSM catalog directory pointed by the GBase PSM_CATALOG configuration
parameter ($GBASEDBTDIR/etc/psm by default) for access permissions.
Check the associated operating system and or C-ISAM errors and correct the
situation.

-88102	GBase PSM catalog creation failed at <DIRECTORY>.

GBase PSM attempted to create the GBase PSM catalog tables in the specified directory, but
failed.

Check associated error messages and resolve the issues.
Check the value of the GBase PSM_CATALOG configuration parameter or environment
variable for the catalog location.

-88103	Error -88103: Cannot open catalog table <TABLE-NAME>.

An attempt to open a catalog table failed with either an C-ISAM error
number or an operating system error number.

Check the accompanying error numbers and correct the situation 
before trying the command again.

-88104	Error -88104: Cannot close catalog table descriptor <FILE-DESCRIPTOR> for table <TABLE-NAME>.

An attempt to close a catalog table failed with either an C-ISAM error
number or an operating system error number.

Check the accompanying error numbers and correct the situation
before trying the command again.

-88105	Error -88105: Cannot lock catalog table <TABLE-NAME>.

An attempt to lock a catalog table failed with either an C-ISAM error
number or an operating system error number.
One reason for this error is that the lock timeout set for GBase PSM is either too 
short or not long enough.

Check the accompanying error numbers and correct the situation
before trying the command again.
Try to make the configuration parameter GBase PSM_TIMEOUT longer if the 
associated C-ISAM error is ELOCKED or EFLOCKED.

-88106	Error -88106: Cannot build a new index for catalog table <TABLE-NAME>.

The storage manager attempted to build an index for the specified catalog table, but
encountered an error.

Check the associated operating system and or C-ISAM errors and correct the
situation.

-88107	Error -88107: Cannot insert record into catalog table <TABLE-NAME>.

The storage manager attempted to insert one or more records to the specified catalog table, but
encountered an error.

Check the associated operating system and or C-ISAM errors and correct the situation.

-88108	Error -88108: Cannot delete records from catalog table <TABLE-NAME>.

The storage manager attempted to delete one or more records from the specified catalog table, but
encountered an error.

Check the associated operating system and or C-ISAM errors and correct the
situation.

-88109	Cannot update to catalog table <TABLE-NAME>.

The storage manager attempted to update one or more records in the specified catalog table, but
encountered an error.

Check the associated operating system and or C-ISAM errors and correct the
situation.

-88110	Cannot read from catalog table <TABLE-NAME>.

The storage manager attempted to read one or more records in the specified catalog table but
encountered an error.

Check the associated operating system and or C-ISAM errors and correct the
situation.

-88111	Cannot get index information for catalog table <TABLE-NAME>.

GBase PSM attempted to obtain index information for a table, but failed.

Check the associated operating system and or C-ISAM errors and correct the
situation.

-88112	Cannot demote the 'HIGHEST' priority device in the pool <POOL NAME>.

GBase PSM attempted to demote the device identified as having the HIGHEST priority in the pool to priority 'HIGH', but an error was returned.

Check the associated operating system and or C-ISAM errors and correct the situation.

-88113	Catalog table <TABLE NAME> is not present.

An attempt to open a catalog table failed because the table is missing.

Check the accompanying error numbers and correct the situation 
before trying the command again.

-88114	The GBase PSM catalog is missing

The GBase PSM catalog is not present in the specified directory

GBase PSM will try to create the catalog.

-88115	The GBase PSM catalog is corrupted.

The GBase PSM catalog is corupted. Either there are missing tables, or the existing tables are damaged.

Manaully inspect that there are no missing tables in the GBase PSM_CATALOG_PATH directory.
Run 'onGBase PSM -C check' command. If this does not solve the issue, import the catalog back.

-88116	The Catalog import operation was aborted.

Multiple problems can cause this problem including an already existing catalog.

Check Associated errors, correct the sittuation and re-run the operation.

-88117	Attempt to load table <TABLE NAME> failed.

GBase PSM attempted to load a catalog table but encontered an error.

Check associatted errors.

-88118	Attempt to erase table <TABLE NAME> failed.

GBase PSM attempted to erase a catalog table but encontered an error.

Check associatted errors.

-88119	The import file has incorrect number of columns in line <LINE NUMBER>.

The unload file being used to load a catalog table has an incorrect number of columns.

Check the file for corruption and fix it or use another load file.

-88130	Error -88130: GBase PSM initialization failed.

The storage manager attempted to obtain index information for a table, but failed.

Check the associated operating system and or C-ISAM errors and correct the
situation.

-88131	Error -88131: The environment variable 'GBASEDBTDIR' is not set.

The storage manager attempted to obtain index information for a table, but failed.

Check the associated operating system and or C-ISAM errors and correct the
situation.

-88132	Error -88132: An attempt to allocate memory to store the value of the 'GBASEDBTDIR'
environment variable failed.

The value of the environment variable might be invalid or corrupted.
Your system might be extremely low in memory.

Check the value of the 'GBASEDBTDIR' environment variable.
Check that your system is not low in memory.

-88133	Error -88133: An attempt to allocate memory to store the value of the 'ONCONFIG'
environment variable failed.

The value of the environment variable might be invalid or corrupted.
Your system might be extremely low in memory.

Check the value of the 'ONCONFIG' environment variable.
Check that your system is not low in memory.

-88134	Error -88134: An attempt to allocate memory to store the value of the 'PSMCONFIG'
environment variable failed.

The value of the environment variable might be invalid or corrupted.
Your system might be extremely low in memory.

Check the value of the 'PSMCONFIG' environment variable.
Check that your system is not low in memory.

-88150	Warning -88150: The BAR_BSALIB_PATH configuration parameter is not set.

The BAR_BSALIB_PATH parameter is not set in the onconfig file. Therefore ON-Bar and all
utilities related to it are using the default path that changes from system to system.
This can lead to problems identifying the location of the library to use.

You must set the BAR_BSALIB_PATH configuration parameter to specify the XBSA library to 
be used by ON-Bar and related utilities. You must set this explicitly in the onconfig file.

-88151	Warning -88151: The XBSA shared library at <LIBRARY-PATH> cannot be loaded.

The XBSA library at the specified location cannot be loaded.

Check the accompanying system error and correct the situation.

-88152	Error -88152: The XBSA shared library at <LIBRARY-PATH> does not seem to be for the
GBase Primary Storage Manager.

The XBSA library at the specified location is not the for the Primary Storage Manager.

Check the BAR_BSALIB_PATH configuration parameter and the default location of the
ON-Bar shared library in your system.

-88160	The converted string <STRING> cannot be for in an INT4 integer.

We are trying to convert a string to INT4 but the value is too big.

Correct the values and retry the operation.

-88161	The length for the string <STRING> is too big to fit in <NUMBER> bytes.

The value that we want to assign to a string exceeds the size of the string variable.

Correct the values and retry the operation in the correct format.

-88162	The system cannot insert the specified device <DEVICE PATH> in the GBase PSM catalog.

The device specified in the operation or command could not be inserted into the GBase PSM catalog.

Check the associated errors for more information related to this problem.

-88163	The pool specified is invalid.

The pool specified is not valid.

Recheck the information that you need and then specify valid values for pools. 

-88164	The device type specified is invalid. 

The device type specified is not valid.

Review device-type information and specify a valid value for the device type.

-88165	The device path is longer than <NUMBER> bytes.

The device path specified is too long.

Specify a valid value for the full path of the device.

-88166	The priority specified for the device is invalid.

The priority specified for the device is invalid.

Specify a valid value for the priority of the device.

-88167	The device specification file has a syntax error at line <LINE-NUMBER>.

Incorrect syntax in the device specification file.

The offending part of the syntax has been ignored.
Correct the syntax and run the command again.

-88168	Loading devices from the device specification file failed.

The operator attempted to create a set of devices from a device specification file, but
the operation failed.

Check the accompanying messages and correct the situation.

-88180	Existing <CATALOG-VERSION> in the catalog is different from <CATALOG-VERSION>.

The GBase PSM catalog was created with a different version of the Primary 
Storage Manager, so processing cannot continue.

Recreate the catalog with the current GBase PSM release.

-88181	Devices of type <DEVICE-TYPE> are not supported in this platform.

Some device types are not supported in all platforms. For example, in the Windows 
operating system, tape devices are not supported.

Refer to the product documentation and use a supported type of device.

-88182	The XBSA Resource Type <RESOURCE-TYPE> is unknown.

The XBSA Resource Type that was specified is unknown to the system.

Refer to the product documentation and use a supported XBSA Resource Type.

-88183	Cannot Acquire a next sequential ID for the object.

GBase PSM tried to generate a serial number to insert a new record in a table but an error happened

Retry the action and contact GBase Technical Support if the problem persist.

-88184	The pool name already exist in the system.

An attempt was made to create a pool that already exist in the system.

Delete the existing pool or try the action again or change the name of the pool that you wnat to create.

-88185	An error happened attempting to create a new pool.

The creation of the pool failed.

See the associated errors and correct the situation.

-88186	A defect in the program was found. Contact GBase Technical Support.

A programming error was found in the program. This error should never happen.

Take note of all circumstances of the error and contact GBase Technical Support immediately.

-88187	The pool that you attempted to delete does no exist.

The pool that you are trying to delete from the system does not exist.

Check the list of existing pools, verify the spelling and syntax and retry the operation.

-88188	The specified pool type <POOL-TYPE-NAME> is invalid.

An incorrect pool type was provided for the requested operation.

Check the manual for a list of valid pool types.

-88189	Close the GBase PSM session failed.

An error happened while trying to close a GBase PSM session.

Check the errors and contact GBase Technical Support.

-88190	Failed to open a GBase PSM session.

An error happened while trying to open a GBase PSM session.

Check the errors and contact GBase Technical Support.

-88191	Failed to generate the list of pools in the system

An error happened while trying to create a list of pools in the system

Check the errors and contact GBase Technical Support.

-88192	Failed to add a pool to the system

An error happened while trying to add a pool to the system

Check the errors and contact GBase Technical Support.

-88193	Failed to delete a pool from the system

An error happened while trying to delete a pool from the system

Check the errors and contact GBase Technical Support.

-88194	System Pools cannot be deleted.

An attempt was made to delete a system pool.

Check the list of system pools in the manual, they cannot be deleted or modified.

-88195	Pools with devices cannot de deleted.

An attempt was made to delete a pool that has devices on it.

Delete the devices that are under the pool and retry the operation.

-88196	The device <DEVICE-PATH> cannot be modified.

An attempt was made to modify a device, but the operation returned an error.

Check that the combination of device path and pool exist in the system, correct any errors, and resubmit the command; or check the additional errors.

-88250	The function received an invalid handle

This is an internal error, a function received an invalid handler and we
cannot continue. This error should not happen.

Contact GBase Technical Support.

-88300	The current user does not have authorization to run this utility.

The current user is not authorized to perform the current action.

The action must be performed by an authorized user.

-88301	The requested action <ACTION> is not valid for the current menu or is an incorrect action.

You tried to perform an invalid action for the current menu (such as "add" while using the catalog menu),
or the attempted action requested is not valid.

Check information on command line options and specify the correct information.

-88302	Duplicated instances of option <COMMAND-LINE-OPTION> are not allowed.

An attempt to have multiple instances of the same option was made, for example, to specify multiple pools for a single device.

Correct the command line information and retry the operation.

-88303	Not enough arguments for option <COMMAND-LINE-OPTION>.

The selected option requires more arguments than those that were provided.

Check command line information and fix the command line.

-88304	No more arguments were expected for the action <ACTION>.

You provided more arguments that were expected.

Check command line information and fix the command line.

-88305	The flag <FLAG> is not valid in the current context.

The specified flag is not valid for the current action.

Check command line information and fix the command line.

-88306	Cannot mix the following flags : <FLAG>

An attempt was made to specify mutually exclusive flags.

Check command line information and fix the command line.

-88307	The provided string <STRING-TEXT> for the argument is longer than <NUMBER> bytes.

An attempt was made to provide a string that is longer than what the program supports.

Use a string of the correct size.

-38602	Server failed to start in recovery mode within an expected time duration.

The server instance started, but did not move into recovery mode in the
allowable time.

ACTION
Check the GBase online log for messages about problems that occurred. Take
the necessary steps to fix those problems, and then rerun the restore utility.
If the problem persists, or no other problems are reported in the online log,
the server might need more time to move into recovery mode.

The restore utility waits 5 minutes, which is 5 times the default value of the
MAX_CONNECT_TRY_RESTORE environment variable. Set the environment variable to
a value greater than  60 seconds, and run the restore utility.

-9659	The server does not support the specified UPDATE operation on JSON documents

Use successive SELECT, DELETE and INSERT operations to update JSON documents

-9660	The database server operation failed due to an invalid JSON document.

Replace the invalid JSON document with a valid JSON document. To prevent the
problem from occurring in the future, ensure that the client inserts valid and
uncorrupted JSON documents into the database. 


-9661	The statement failed because constraint (%s) cannot be enabled with 
NOVALIDATE option.

Only foreign key constraints are allowed to be enabled with NOVALIDATE option.
Examine the sysconstraints.constrtype entry for this constraint in the system
catalog. Unless the value for this constraint is R, it is not a referential
constraint. Remove the NOVALIDATE keyword from the statement. 

-9662	No user permission for %s

The user/tenant in multitenancy mode is restricted to use only the
resources provided by the application provider. These resources can
be dbspaces, blobspaces, sbspaces, temp dbspaces, temp sbspaces etc..
In this case, the user/tenant is not permitted to use the resource.

-961	The password is too simple.

-962	Password is out of date. The user is locked. 

-963	The user is locked. 

-964	The user is not known on the database server.

-965	Illeagl time for the user to login.

-966	Security authtication model is invalid by its error.

-967	The password is not set.
-9664   The length of rowtype exceeds the maximum length.
-9665	Max variable number of procedure exceeded.

-10112	The syntax is ora style.
-26981	Invalid label label-name used with CONTINUE/EXIT WHEN clause.

The label is not defined or is not a valid label for the LOOP statement.
The CONTINUE/EXIT WHEN label must be defined within a scope that the LOOP
statement can reach.  Check the label-names for correct spelling.

-26750	The fragment key in hash startegy should be column, not other expr.

-26751	Could not create table - duplicate column names in fragment key.

-26752	Could not alter table column's type or len which is in fragment key.

-26753	Could not create table - fragment key could not be TEXT/BYTE/BLOB/CLOB.

---------
