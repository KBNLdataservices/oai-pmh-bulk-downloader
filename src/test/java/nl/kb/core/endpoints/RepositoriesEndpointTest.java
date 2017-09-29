package nl.kb.core.endpoints;

import com.google.common.collect.Lists;
import nl.kb.core.model.repository.HarvestSchedule;
import nl.kb.core.model.repository.Repository;
import nl.kb.core.model.repository.RepositoryController;
import nl.kb.core.model.repository.RepositoryDao;
import nl.kb.core.model.repository.RepositoryValidator;
import nl.kb.http.HttpResponseException;
import org.junit.Test;
import org.mockito.InOrder;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepositoriesEndpointTest {

    @Test
    public void createShouldCreateANewRpository() {
        final RepositoryDao dao = mock(RepositoryDao.class);
        final RepositoryController repositoryController = mock(RepositoryController.class);
        final RepositoriesEndpoint instance = new RepositoriesEndpoint(dao, mock(RepositoryValidator.class), repositoryController);
        final Repository repositoryConfig = new Repository.RepositoryBuilder().setUrl("http://example.com").setName("name").setMetadataPrefix("prefix").setSet("setname").setDateStamp("123").setEnabled(true).setSchedule(HarvestSchedule.DAILY).createRepository();
        final Response response = instance.create(repositoryConfig);

        verify(dao).insert(repositoryConfig);
        verify(repositoryController).notifyUpdate();
        assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));
        assertThat(response.getHeaderString("Location"), equalTo("/repositories"));
    }

    @Test
    public void deleteShouldDeleteTheRepositoryAndItsRecords() throws IOException {
        final RepositoryDao dao = mock(RepositoryDao.class);
        final RepositoryController repositoryController = mock(RepositoryController.class);
        final RepositoriesEndpoint instance = new RepositoriesEndpoint(dao, mock(RepositoryValidator.class), repositoryController);
        final Integer id = 123;

        final Response response = instance.delete(id);

        final InOrder inOrder = inOrder(dao, repositoryController);
        inOrder.verify(dao).remove(id);
        inOrder.verify(repositoryController).notifyUpdate();
        inOrder.verifyNoMoreInteractions();
        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }


    @Test
    public void updateShouldUpdateTheRepository() {
        final RepositoryDao dao = mock(RepositoryDao.class);
        final RepositoryController repositoryController = mock(RepositoryController.class);
        final RepositoriesEndpoint instance = new RepositoriesEndpoint(dao, mock(RepositoryValidator.class), repositoryController);
        final Repository repositoryConfig = new Repository.RepositoryBuilder().setUrl("http://example.com").setName("name").setMetadataPrefix("prefix").setSet("setname").setDateStamp("123").setEnabled(true).setSchedule(HarvestSchedule.DAILY).createRepository();
        final Integer id = 123;

        final Response response = instance.update(id, repositoryConfig);

        verify(dao).update(id, repositoryConfig);
        verify(repositoryController).notifyUpdate();

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        assertThat(response.getEntity(), equalTo(repositoryConfig));
    }

    @Test
    public void enableShouldUpdateTheRepository() {
        final RepositoryDao dao = mock(RepositoryDao.class);
        final RepositoryController repositoryController = mock(RepositoryController.class);
        final RepositoriesEndpoint instance = new RepositoriesEndpoint(dao, mock(RepositoryValidator.class), repositoryController);
        final Integer id = 123;

        final Response response = instance.enable(id);

        verify(dao).enable(id);
        verify(repositoryController).notifyUpdate();

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void disableShouldUpdateTheRepository() {
        final RepositoryDao dao = mock(RepositoryDao.class);
        final RepositoryController repositoryController = mock(RepositoryController.class);
        final RepositoriesEndpoint instance = new RepositoriesEndpoint(dao, mock(RepositoryValidator.class), repositoryController);
        final Integer id = 123;

        final Response response = instance.disable(id);

        verify(dao).disable(id);
        verify(repositoryController).notifyUpdate();

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }


    @Test
    public void indexShouldRespondWithAListOfRepositories() {
        final RepositoryDao dao = mock(RepositoryDao.class);
        final RepositoriesEndpoint instance = new RepositoriesEndpoint(dao, mock(RepositoryValidator.class), mock(RepositoryController.class));
        final Repository repositoryConfig1 = new Repository.RepositoryBuilder().setUrl("http://example.com").setName("name").setMetadataPrefix("prefix").setSet("setname").setDateStamp("123").setEnabled(true).setSchedule(HarvestSchedule.DAILY).setId(1).setLastHarvest(null).createRepository();
        final Repository repositoryConfig2 = new Repository.RepositoryBuilder().setUrl("http://example.com").setName("name").setMetadataPrefix("prefix").setSet("setname").setDateStamp("123").setEnabled(true).setSchedule(HarvestSchedule.DAILY).setId(2).setLastHarvest(null).createRepository();
        final List<Repository> repositories = Lists.newArrayList(repositoryConfig1, repositoryConfig2);

        when(dao.list()).thenReturn(repositories);
        final Response response = instance.index();

        verify(dao).list();
        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        assertThat(response.getEntity(), equalTo(repositories));
    }


    @Test
    public void validateNewShouldReturnTheValidationResultForTheRepositoryConfiguration() throws IOException, SAXException, HttpResponseException {
        final RepositoryDao dao = mock(RepositoryDao.class);
        final RepositoryValidator validator = mock(RepositoryValidator.class);
        final RepositoriesEndpoint instance = new RepositoriesEndpoint(dao, validator, mock(RepositoryController.class));
        final Repository repositoryConfig = new Repository.RepositoryBuilder().setUrl("http://example.com").setName("name").setMetadataPrefix("prefix").setSet("setname").setDateStamp("123").setEnabled(true).setSchedule(HarvestSchedule.DAILY).createRepository();
        final RepositoryValidator.ValidationResult validationResult = validator.new ValidationResult();
        when(validator.validate(repositoryConfig)).thenReturn(validationResult);

        final Response response = instance.validateNew(repositoryConfig);

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        assertThat(response.getEntity(), equalTo(validationResult));
    }
}