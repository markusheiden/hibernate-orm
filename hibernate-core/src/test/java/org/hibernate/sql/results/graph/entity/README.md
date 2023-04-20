# HHH-16286

See https://hibernate.atlassian.net/jira/software/c/projects/HHH/issues/HHH-16286?filter=allissues

## Location

EmbeddableFetchInitializer(com.adsoul.seasupport.keyword.text.ResponsiveTextTemplate.sharedPaths.{element}) : `class com.adsoul.seasupport.keyword.text.TextComponentLink$SharedTextComponentLink`

## Stacktrace

```
java.lang.NullPointerException: Cannot invoke "org.hibernate.sql.results.graph.entity.EntityInitializer.getNavigablePath()" because "firstEntityInitializer" is null
	at org.hibernate.sql.results.graph.entity.internal.BatchEntityInsideEmbeddableSelectFetchInitializer.getRootEmbeddablePropertyName(BatchEntityInsideEmbeddableSelectFetchInitializer.java:190)
	at org.hibernate.sql.results.graph.entity.internal.BatchEntityInsideEmbeddableSelectFetchInitializer.<init>(BatchEntityInsideEmbeddableSelectFetchInitializer.java:57)
	at org.hibernate.sql.results.graph.entity.internal.EntitySelectFetchInitializerBuilder.createInitializer(EntitySelectFetchInitializerBuilder.java:58)
	at org.hibernate.sql.results.graph.entity.internal.EntityFetchSelectImpl.buildEntitySelectFetchInitializer(EntityFetchSelectImpl.java:92)
	at org.hibernate.sql.results.graph.entity.internal.EntityFetchSelectImpl.lambda$createAssembler$0(EntityFetchSelectImpl.java:70)
	at org.hibernate.sql.results.internal.ResultsHelper$1.resolveInitializer(ResultsHelper.java:108)
	at org.hibernate.sql.results.graph.entity.internal.EntityFetchSelectImpl.createAssembler(EntityFetchSelectImpl.java:67)
	at org.hibernate.sql.results.graph.embeddable.AbstractEmbeddableInitializer.initializeAssemblers(AbstractEmbeddableInitializer.java:121)
	at org.hibernate.sql.results.graph.embeddable.AbstractEmbeddableInitializer.<init>(AbstractEmbeddableInitializer.java:107)
	at org.hibernate.sql.results.graph.embeddable.internal.EmbeddableFetchInitializer.<init>(EmbeddableFetchInitializer.java:23)
	at org.hibernate.sql.results.graph.embeddable.internal.EmbeddableFetchImpl.buildEmbeddableFetchInitializer(EmbeddableFetchImpl.java:159)
	at org.hibernate.sql.results.graph.embeddable.internal.EmbeddableFetchImpl.lambda$createAssembler$1(EmbeddableFetchImpl.java:147)
	at org.hibernate.sql.results.internal.ResultsHelper$1.resolveInitializer(ResultsHelper.java:108)
	at org.hibernate.sql.results.graph.embeddable.internal.EmbeddableFetchImpl.createAssembler(EmbeddableFetchImpl.java:144)
	at org.hibernate.sql.results.graph.collection.internal.ListInitializerProducer.produceInitializer(ListInitializerProducer.java:54)
	at org.hibernate.sql.results.graph.collection.internal.CollectionDomainResult.lambda$createResultAssembler$0(CollectionDomainResult.java:100)
	at org.hibernate.sql.results.internal.ResultsHelper$1.resolveInitializer(ResultsHelper.java:108)
	at org.hibernate.sql.results.graph.collection.internal.CollectionDomainResult.createResultAssembler(CollectionDomainResult.java:94)
	at org.hibernate.sql.results.jdbc.internal.StandardJdbcValuesMapping.resolveAssemblers(StandardJdbcValuesMapping.java:53)
	at org.hibernate.sql.results.internal.ResultsHelper.createRowReader(ResultsHelper.java:78)
	at org.hibernate.sql.results.internal.ResultsHelper.createRowReader(ResultsHelper.java:64)
	at org.hibernate.sql.exec.internal.JdbcSelectExecutorStandardImpl.doExecuteQuery(JdbcSelectExecutorStandardImpl.java:341)
	at org.hibernate.sql.exec.internal.JdbcSelectExecutorStandardImpl.executeQuery(JdbcSelectExecutorStandardImpl.java:168)
	at org.hibernate.sql.exec.internal.JdbcSelectExecutorStandardImpl.list(JdbcSelectExecutorStandardImpl.java:93)
	at org.hibernate.sql.exec.spi.JdbcSelectExecutor.list(JdbcSelectExecutor.java:31)
	at org.hibernate.loader.ast.internal.CollectionLoaderSubSelectFetch.load(CollectionLoaderSubSelectFetch.java:130)
	at org.hibernate.persister.collection.AbstractCollectionPersister.initialize(AbstractCollectionPersister.java:668)
	at org.hibernate.event.internal.DefaultInitializeCollectionEventListener.onInitializeCollection(DefaultInitializeCollectionEventListener.java:75)
	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:127)
	at org.hibernate.internal.SessionImpl.initializeCollection(SessionImpl.java:1697)
	at org.hibernate.collection.spi.AbstractPersistentCollection.lambda$initialize$3(AbstractPersistentCollection.java:617)
	at org.hibernate.collection.spi.AbstractPersistentCollection.withTemporarySessionIfNeeded(AbstractPersistentCollection.java:265)
	at org.hibernate.collection.spi.AbstractPersistentCollection.initialize(AbstractPersistentCollection.java:615)
	at org.hibernate.collection.spi.AbstractPersistentCollection.forceInitialization(AbstractPersistentCollection.java:813)
	at org.hibernate.engine.internal.StatefulPersistenceContext.initializeNonLazyCollections(StatefulPersistenceContext.java:985)
	at org.hibernate.engine.internal.StatefulPersistenceContext.initializeNonLazyCollections(StatefulPersistenceContext.java:971)
	at org.hibernate.sql.results.spi.ListResultsConsumer.consume(ListResultsConsumer.java:227)
	at org.hibernate.sql.results.spi.ListResultsConsumer.consume(ListResultsConsumer.java:33)
	at org.hibernate.sql.exec.internal.JdbcSelectExecutorStandardImpl.doExecuteQuery(JdbcSelectExecutorStandardImpl.java:362)
	at org.hibernate.sql.exec.internal.JdbcSelectExecutorStandardImpl.executeQuery(JdbcSelectExecutorStandardImpl.java:168)
	at org.hibernate.sql.exec.internal.JdbcSelectExecutorStandardImpl.list(JdbcSelectExecutorStandardImpl.java:93)
	at org.hibernate.sql.exec.spi.JdbcSelectExecutor.list(JdbcSelectExecutor.java:31)
	at org.hibernate.loader.ast.internal.CollectionLoaderSubSelectFetch.load(CollectionLoaderSubSelectFetch.java:130)
	at org.hibernate.persister.collection.AbstractCollectionPersister.initialize(AbstractCollectionPersister.java:668)
	at org.hibernate.event.internal.DefaultInitializeCollectionEventListener.onInitializeCollection(DefaultInitializeCollectionEventListener.java:75)
	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:127)
	at org.hibernate.internal.SessionImpl.initializeCollection(SessionImpl.java:1697)
	at org.hibernate.collection.spi.AbstractPersistentCollection.lambda$initialize$3(AbstractPersistentCollection.java:617)
	at org.hibernate.collection.spi.AbstractPersistentCollection.withTemporarySessionIfNeeded(AbstractPersistentCollection.java:265)
	at org.hibernate.collection.spi.AbstractPersistentCollection.initialize(AbstractPersistentCollection.java:615)
	at org.hibernate.collection.spi.AbstractPersistentCollection.forceInitialization(AbstractPersistentCollection.java:813)
	at org.hibernate.Hibernate.initialize(Hibernate.java:134)
```