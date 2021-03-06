package fr.yodamad.svn2git.web.rest;

import fr.yodamad.svn2git.Svn2GitApp;
import fr.yodamad.svn2git.data.Repository;
import fr.yodamad.svn2git.domain.SvnInfo;
import fr.yodamad.svn2git.domain.SvnStructure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static fr.yodamad.svn2git.data.Repository.Modules.MODULE_1;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Svn2GitApp.class)
public class SvnResourceTest {

    @Autowired
    private SvnResource svnResource;

    public static final SvnInfo svnInfo = new SvnInfo();
    static {
        svnInfo.url = "https://chaos.yodamad.fr/svn";
        svnInfo.user = "demo";
        svnInfo.password = "demo";
    }

    @Test
    public void test_svn_listing_on_flat_repo() {
        SvnStructure svnStructure = svnResource.listSVN(svnInfo, Repository.simple().name);
        assertThat(svnStructure.modules).isEmpty();
        assertThat(svnStructure.flat).isTrue();
    }

    @Test
    public void test_svn_listing_on_complex_repo() {
        SvnStructure svnStructure = svnResource.listSVN(svnInfo, Repository.complex().name);
        assertThat(svnStructure.modules).isNotEmpty();
        assertThat(svnStructure.flat).isFalse();
        List<SvnStructure.SvnModule> modules = svnStructure.modules;
        assertThat(modules.size()).isEqualTo(3);
        modules.forEach(
            m -> {
                assertThat(m.name).isIn(Repository.ALL_MODULES);
                if (MODULE_1.equals(m.name)) {
                    assertThat(m.subModules).isNotEmpty();
                    assertThat(m.subModules.size()).isEqualTo(2);
                }
            }
        );
    }
}
