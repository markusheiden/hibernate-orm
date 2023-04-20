package org.hibernate.sql.results.graph.entity;

import java.util.List;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

@DomainModel(annotatedClasses = {
        AbstractLocal.class,
        AbstractLocalKeyword.class,
        AbstractLocalResponsiveText.class,
        AbstractLocalResponsiveTextLine.class,
        AbstractTextComponent.class,
        CustomTextComponent.class,
        LocalAccount.class,
        LocalCampaign.class,
        LocalGroup.class,
        LocalKeyword.class,
        LocalManualResponsiveText.class,
        LocalManualResponsiveTextLine.class,
        LocalMetaGroup.class,
        LocalNegative.class,
        LocalResponsiveText.class,
        LocalResponsiveTextLine.class,
        MasterTextTemplate.class,
        Portal.class,
        Promotion.class,
        ResponsiveTextTemplate.class,
        SharedTextComponent.class,
        TextComponentLink.class,
        TrackingParameters.class
})
@SessionFactory
@TestForIssue(jiraKey = "HHH-16286")
public class EntityInitializerTest {
    @Test
    void test(SessionFactoryScope scope) {
        var portal = new Portal();
        portal.setName("portal");
        portal.setHost("portal.de");
        scope.inTransaction(session -> {
            session.persist(portal);
        });

        var shared1 = new SharedTextComponent(portal.getId(), null, TextKind.HEADLINE, List.of("line1", "line2"), List.of());
        var shared2 = new SharedTextComponent(portal.getId(), null, TextKind.HEADLINE, List.of("line3", "line4"), List.of());
        var master = new MasterTextTemplate(portal.getId());
        var template1 = new ResponsiveTextTemplate("1");
        template1.setMasterTemplate(master);
        template1.add(shared1);
        var template2 = new ResponsiveTextTemplate("2");
        template2.setMasterTemplate(master);
        template2.add(shared2);

        var account = new LocalAccount(portal, "account");
        var campaign = new LocalCampaign(account, false, "campaign");
        var metaGroup = new LocalMetaGroup(campaign, false);
        var group = new LocalGroup(metaGroup, MatchType.EXACT, "1");
        var text1 = new LocalResponsiveText(template1);
        text1.setHeadlines(List.of(new LocalResponsiveTextLine(shared1, 0, "line1", null)));
        group.addResponsiveText(text1);
        var text2 = new LocalResponsiveText(template2);
        text2.setHeadlines(List.of(new LocalResponsiveTextLine(shared1, 0, "line1", null)));
        group.addResponsiveText(text2);

        scope.inTransaction(session -> {
            session.persist(master);
            session.persist(text1);
            session.persist(text2);
            session.persist(group);
            session.persist(metaGroup);
            session.persist(campaign);
            session.persist(account);
        });
        scope.inTransaction(session -> {
            var t1 = session.find(LocalResponsiveText.class, text1.getId());
            var t2 = session.find(LocalResponsiveText.class, text2.getId());
            t1.remove();
            session.remove(t1);
            LazyLoadingUtil.deepHydrate(session, text2);
            t2.remove();
            session.remove(t2);
        });
    }
}
