set DATABASE_URL=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
for /f "usebackq tokens=*" %%i in (`heroku config:get SENDGRID_USERNAME`) do @set SENDGRID_USERNAME=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get SENDGRID_PASSWORD`) do @set SENDGRID_PASSWORD=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get REDISCLOUD_URL`) do @set REDISCLOUD_URL=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get CLOUDINARY_URL`) do @set CLOUDINARY_URL=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get HYPDF_USER`) do @set HYPDF_USER=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get HYPDF_PASSWORD`) do @set HYPDF_PASSWORD=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get AUTH0_CLIENT_ID`) do @set AUTH0_CLIENT_ID=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get AUTH0_CLIENT_SECRET`) do @set AUTH0_CLIENT_SECRET=%%i
for /f "usebackq tokens=*" %%i in (`heroku config:get AUTH0_DOMAIN`) do @set AUTH0_DOMAIN=%%i
set AUTH0_CALLBACK_URL=http://localhost:9000/auth/callback
