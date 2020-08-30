package org.example.ticketcampaign

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import spock.lang.Specification

import javax.persistence.EntityManager

@SpringBootTest
@Transactional
@ActiveProfiles('integration')
abstract class BaseIntegrationTest extends Specification {

    @Autowired
    private EntityManager entityManager

    def cleanup() {
        // since some of the DB columns have integrity constraints defined, perform last-chance flush before rollback to detect violations
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            entityManager.flush()
        }
    }
}
