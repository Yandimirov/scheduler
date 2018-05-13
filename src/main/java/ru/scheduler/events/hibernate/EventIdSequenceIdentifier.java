package ru.scheduler.events.hibernate;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import ru.scheduler.events.model.CompositeId;
import ru.scheduler.events.model.CompositeIdentifiable;
import ru.scheduler.events.model.entity.Event.EventId;

import java.io.Serializable;
import java.util.Properties;

public class EventIdSequenceIdentifier extends SequenceStyleGenerator {

    private String sequenceCallSyntax;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
            throws MappingException {

        if (type.getReturnedClass() != EventId.class) {
            throw new IllegalArgumentException(
                    String.format(
                            "Generator '%s' could not be applied to '%s' class. Expected: '%s' class",
                            this.getClass().getName(), type.getReturnedClass().getName(),
                            EventId.class.getName()
                    )
            );
        }

        final JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        final Dialect dialect = jdbcEnvironment.getDialect();

        sequenceCallSyntax = dialect.getSequenceNextValString(
                ConfigurationHelper.getString(
                        SequenceStyleGenerator.SEQUENCE_PARAM,
                        params,
                        SequenceStyleGenerator.DEF_SEQUENCE_NAME));

        super.configure(new LongType(), params, serviceRegistry);
    }

    @Override
    public Serializable generate(SessionImplementor session, Object obj) {
        CompositeIdentifiable identifiable = (CompositeIdentifiable) obj;
        CompositeId compositeId = identifiable.getCompositeId();

        if (compositeId != null && compositeId.getId() != 0) {
            return compositeId;
        }

        long seqValue = ((Number) Session.class.cast(session)
                .createSQLQuery(sequenceCallSyntax)
                .uniqueResult()).longValue();

        return new EventId(seqValue);
    }
}