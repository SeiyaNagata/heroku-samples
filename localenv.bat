set DATABASE_URL=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
for /f "usebackq tokens=*" %%i in (`heroku config:get SENDGRID_USERNAME`) do @set SENDGRID_USERNAME=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get SENDGRID_PASSWORD`) do @set SENDGRID_PASSWORD=%%i