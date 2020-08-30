### Building the project

Use `mvn clean install` to build the project (note that integration tests require a valid Docker installation)

### Running the application

1. Run `docker-compose up` from the project root to create a PostgreSQL container (or set up a db yourself and provide correct credentials in `application.yaml`) 
2. Start the app by running `TicketCampaignApplication` from your IDE or use `mvn spring-boot:run`
3. Navigate to `http://localhost:8080/swagger-ui/` for a list of available endpoints