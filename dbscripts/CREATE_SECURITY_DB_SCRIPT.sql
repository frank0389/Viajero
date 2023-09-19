------------------------------------------------------------------------------------------------------------------
--- 						SECURITY TABLES 																	--
------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS UM_USER (
	ID INTEGER NOT NULL,
	user_uuid VARCHAR(100) NOT NULL,
	user_name VARCHAR (50) UNIQUE NOT NULL,
	password_hash VARCHAR (60) NOT NULL,
	first_name VARCHAR (50),
	last_name VARCHAR (50),
	dni VARCHAR(255) NOT NULL,
	email VARCHAR (320) UNIQUE NOT NULL,
	phone VARCHAR (15),
    company VARCHAR(255),
	activated BOOLEAN DEFAULT FALSE,
	block BOOLEAN DEFAULT FALSE,
	lang_key VARCHAR (10),
	activation_key VARCHAR (20),
	reset_key VARCHAR (20),
	reset_date TIMESTAMP,
	created_by VARCHAR (50),
	created_date TIMESTAMP NOT NULL ,
	last_modified_by VARCHAR (50),
	last_modified_date TIMESTAMP ,
	metadata JSON DEFAULT '{}',
	CONSTRAINT user_pk PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS UM_ROLE (
	ID INTEGER NOT NULL,
	name VARCHAR (20) UNIQUE NOT NULL,
	CONSTRAINT role_pk  PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS UM_USER_ROLE (
	ID INTEGER NOT NULL ,
	role_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	CONSTRAINT user_role_pk PRIMARY KEY (ID),
	CONSTRAINT user_role_role_fk FOREIGN KEY (role_id) REFERENCES UM_ROLE(ID) ON DELETE CASCADE,
	CONSTRAINT user_role_user_fk FOREIGN KEY (user_id) REFERENCES UM_USER (ID) ON DELETE CASCADE
);

CREATE TYPE EMAIL_TYPE AS ENUM (
'AccountActivation',
'AccountConfirmation',
'PasswordReset',
'AccountRecovery',
'AccountLogin'
);

CREATE TABLE IF NOT EXISTS UM_EMAIL_TEMPLATE (
	ID INTEGER NOT NULL,
	subject VARCHAR (255) NOT NULL,
	body TEXT NOT NULL,
	en_subject VARCHAR (255) NOT NULL,
	en_body TEXT NOT NULL,
	TYPE EMAIL_TYPE  NOT NULL,
	CONSTRAINT email_template_pk PRIMARY KEY (ID)
);


------------------------------------- SEQUENCES -----------------------------------------------------------------------------
CREATE SEQUENCE SEQ_USER;
ALTER TABLE UM_USER ALTER COLUMN ID SET DEFAULT nextval('SEQ_USER'::regclass);

CREATE SEQUENCE SEQ_ROLE;
ALTER TABLE UM_ROLE ALTER COLUMN ID SET DEFAULT nextval('SEQ_ROLE'::regclass);

CREATE SEQUENCE SEQ_USER_ROLE;
ALTER TABLE UM_USER_ROLE ALTER COLUMN ID SET DEFAULT nextval('SEQ_USER_ROLE'::regclass);

CREATE SEQUENCE SEQ_EMAIL_TEMPLATE;
ALTER TABLE UM_EMAIL_TEMPLATE ALTER COLUMN ID SET DEFAULT nextval('SEQ_EMAIL_TEMPLATE'::regclass);

--------------------------------------------- INDEX ---------------------------------------------------------------------------------
CREATE UNIQUE INDEX IDX_USER_ID ON UM_USER(ID);
CREATE UNIQUE INDEX IDX_USER_NAME ON UM_USER(user_name);
CREATE UNIQUE INDEX IDX_USER_UUID ON UM_USER(user_uuid);
CREATE UNIQUE INDEX IDX_USER_EMAIL ON UM_USER(email);

----------------------------------------- INSERT SENTENCES ------------------------------------------------------------------

-- Admin user password: admViajando*123
INSERT INTO UM_USER(
	user_uuid, user_name, password_hash, first_name, last_name, dni, email, phone, company, 
	activated, block, lang_key, activation_key, reset_key, reset_date, created_by, created_date,
	last_modified_date)
	VALUES ( 'a6ea1523-30e3-4e5d-b19e-c83232f65102', 'admin', '$2a$12$LArj8dVWY./bYJuBO56bz.N9EiwNIfGJUd3KHJeXm8rzUVFzD2anK', 'Frank Enrique', 
			'Nicolau González', 'k799342', 'frank.nicolau03@gmail.com', '+593962043962', NULL, true, false, 'es', null, null, null, null, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
			
INSERT INTO UM_USER(
	user_uuid, user_name, password_hash, first_name, last_name, dni, email, phone, company, 
	activated, block, lang_key, activation_key, reset_key, reset_date, created_by, created_date,
	last_modified_date)
	VALUES ( '24fadc60-c581-4f97-9903-0e7df555a133', 'system', 'system', 'Enrique', 
			'Nicolau', 'k799342', 'frank.nicolau@dyamanto.com', '+593942043962', NULL, true, false, 'es', null, null, null, null, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

					
INSERT INTO UM_ROLE(
	 name)
	VALUES ('admin');
	
INSERT INTO UM_ROLE(
	 name)
	VALUES ('operator');	

INSERT INTO UM_ROLE(
	 name)
	VALUES ( 'user');	
	
	
INSERT INTO UM_USER_ROLE(
	 role_id, user_id)
	VALUES ( 1, 1);	
	
INSERT INTO UM_USER_ROLE(
	 role_id, user_id)
	VALUES ( 2, 1);		

INSERT INTO UM_USER_ROLE(
	 role_id, user_id)
	VALUES ( 1, 2);	
	
INSERT INTO UM_EMAIL_TEMPLATE(
	"subject",
	"body",
	"en_subject",
	"en_body",
	"type"
)
VALUES
	( 'Activar cuenta', '<html>
  <head>
  </head>
  <body>
    <p >Estimad@ $firstName</p>
    <p >Su cuenta ha sido creada, Copie el codigo que aparece a continuación para activarla:</p>
    <p>
       $code
    </p>
    <p>
      <span>Saludos, Equipo de Identidad de Viajemos.</span>
      <br />
    </p>
  </body>
</html>',
'Account activation',
 '<html>
  <head>
  </head>
  <body>
    <p >Dear@ $firstName</p>
    <p >Your account has been created, Copy the code below to activate it.</p>
    <p>
       $code
    </p>
    <p>
      <span>Greetings, Viajemos Identity Team.</span>
      <br />
    </p>
  </body>
</html>',
'AccountActivation');

INSERT INTO UM_EMAIL_TEMPLATE (
	"subject",
	"body",
	"en_subject",
	"en_body",
	"type"
)
VALUES
	( 'Confirmación de cuenta', '<html>
  <head>
  </head>
  <body>
    <p >Estimad@  $firstName</p>
    <p >Su cuenta ha sido creada con éxito. </p>
    <p>
      <span>Saludos, Equipo de Identidad de Viajemos.</span>
      <br />
    </p>
  </body>
</html>',
'Account confirmation',
'<html>
  <head>
  </head>
  <body>
    <p >Dear@  $firstName</p>
    <p >Your account has been created successfully. </p>
    <p>
      <span>Greetings, Viajemos Identity Team.</span>
      <br />
    </p>
  </body>
</html>',
 'AccountConfirmation');

INSERT INTO UM_EMAIL_TEMPLATE (
	"subject",
	"body",
	"en_subject",
	"en_body",
	"type"
)
VALUES
	( 'Cambiar contraseña', '<html>
  <head>
  </head>
  <body>
    <p >Estimad@ $firstName</p>
    <p >Recibimos una solicitud para restablecer la contraseña de la cuenta $userName, por favor copie el codigo que aparece a continuación para restablecer su contraseña.</p>
	 <p>
       $code
    </p>
    <p>
      <span>Saludos, Equipo de Identidad de Viajemos.</span>
      <br />
    </p>
  </body>
</html>',
'Change Password',
'<html>
  <head>
  </head>
  <body>
    <p >Dear@ $firstName</p>
    <p >We received a request to reset the password for the account $userName, please copy the code below to reset your password.</p>
	 <p>
       $code
    </p>
    <p>
      <span>Greetings, Viajemos Identity Team.</span>
      <br />
    </p>
  </body>
</html>',
 'PasswordReset');

INSERT INTO UM_EMAIL_TEMPLATE (
	"subject",
	"body",
	"en_subject",
	"en_body",
	"type"
)
VALUES
	('Recuperar cuenta', '<html>
  <head>
  </head>
  <body>
    <p >Estimad@ $firstName</p>
    <p >Recibimos una solicitud para recuperar su cuenta.</p>
	 <p>
        Usuario: $userName
    </p>
    <p>
      <span>Saludos, Equipo de Identidad de Viajemos. </span>
      <br />
    </p>
  </body>
</html>',
'Recover account',
'<html>
  <head>
  </head>
  <body>
    <p >Dear@ $firstName</p>
    <p >We received a request to recover your account.</p>
	 <p>
        User: $userName
    </p>
    <p>
      <span>Greetings, Viajemos Identity Team. </span>
      <br />
    </p>
  </body>
</html>',
 'AccountRecovery');	

INSERT INTO UM_EMAIL_TEMPLATE(
	"subject",
	"body",
	"en_subject",
	"en_body",
	"type"
)
VALUES
	( 'Inicio de sesión', '<html>
  <head>
  </head>
  <body> 
    <p >Estimad@ $firstName</p>
    <p >Detectamos un inicio de sesión en su cuenta <b style=''color:black;''> $userName </b> el día $date.</p>
    <p>
      Enviamos este correo electrónico para asegurarnos de que haya sido usted.
    </p>
	<p>
	    Si reconoce este inicio:   <br />
	 	No es necesario hacer nada, puede ignorar este correo.
	 </p>
	 <p>
       Si no ha sido usted:   <br />
      Le recomendamos cambiar su <b style=''color:red;''> CONTRASEÑA DE ACCESO </b> lo antes posible.
	</p>
    <p>
      <span>Saludos, Equipo de Identidad de Viajemos.</span>
      <br />
    </p>
  </body>
</html>',
'Login detected',
'<html>
  <head>
  </head>
  <body> 
    <p >Dear@ $firstName</p>
    <p >We detected a login to your account <b style=''color:black;''> $userName </b> in $date.</p>
    <p>
      We are sending this email to make sure you are logged into the system..
    </p>
	<p>
	    If you recognize this session login:   <br />
	 	You don''t need to do anything, you can ignore this email.
	 </p>
	 <p>
      If you are not logged in:   <br />
      We recommend that change your <b style=''color:red;''> ACCESS PASSWORD </b> as soon as possible.
	</p>
    <p>
      <span>Greetings, Viajemos Identity Team.</span>
      <br />
    </p>
  </body>
</html>',
 'AccountLogin');
	