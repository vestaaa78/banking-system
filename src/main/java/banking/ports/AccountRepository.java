package banking.ports;

public interface AccountRepository extends
        AccountReader,
        AccountWriter,
        AccountDeleter,
        AccountCatalog {
}