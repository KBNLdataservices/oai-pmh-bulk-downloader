package nl.kb.dare.model.repository;

import nl.kb.dare.websocket.SocketNotifier;
import nl.kb.dare.websocket.socketupdate.RepositoryUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryController {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryController.class);

    private final RepositoryDao repositoryDao;
    private final SocketNotifier socketNotifier;

    public RepositoryController(RepositoryDao repositoryDao, SocketNotifier socketNotifier) {
        this.repositoryDao = repositoryDao;
        this.socketNotifier = socketNotifier;
    }


    public void notifyUpdate() {
        socketNotifier.notifyUpdate(new RepositoryUpdate(repositoryDao.list()));
    }

    public void onHarvestComplete(Integer id, String dateStamp) {
        synchronized (repositoryDao) {
            repositoryDao.setDateStamp(id, dateStamp);
        }
        notifyUpdate();
    }

    public void onHarvestException(Integer id, Exception exception) {
        LOG.error("Harvest failed for repository with id {}", id, exception);
        synchronized (repositoryDao) {
            repositoryDao.disableAll();
        }
        notifyUpdate();
    }

    public void onHarvestProgress(Integer id, String dateStamp) {
        synchronized (repositoryDao) {
            repositoryDao.setDateStamp(id, dateStamp);
        }
        notifyUpdate();
    }

    public void beforeHarvest(Integer id) {
        synchronized (repositoryDao) {
            repositoryDao.setLastHarvest(id);
        }
        notifyUpdate();
    }

}
