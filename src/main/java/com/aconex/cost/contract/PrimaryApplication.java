package com.aconex.cost.contract;

import com.aconex.cost.contract.config.ApplicationConfiguration;
import com.aconex.cost.contract.controllers.ContractsController;
import com.aconex.cost.contract.models.Contract;
import com.aconex.cost.contract.repositories.ContractRepository;
import com.aconex.cost.contract.services.ContractService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class PrimaryApplication extends Application<ApplicationConfiguration> {

    private final HibernateBundle<ApplicationConfiguration> hibernateBundle = new HibernateBundle<ApplicationConfiguration>(Contract.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(ApplicationConfiguration applicationConfiguration) {
            return applicationConfiguration.getDataSourceFactory();
        }
    };

    private final MigrationsBundle<ApplicationConfiguration> migrationsBundle = new MigrationsBundle<ApplicationConfiguration>() {
        @Override
        public DataSourceFactory getDataSourceFactory(ApplicationConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private final ViewBundle<ApplicationConfiguration> viewBundle = new ViewBundle<>();

    private final AssetsBundle assetsBundle = new AssetsBundle("/assets", "/assets");

    public static void main(String[] args) throws Exception {
        new PrimaryApplication().run(args);
    }

    @Override
    public String getName() {
        return "contract-management";
    }

    @Override
    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(migrationsBundle);
        bootstrap.addBundle(viewBundle);
        bootstrap.addBundle(assetsBundle);
    }

    @Override
    public void run(ApplicationConfiguration applicationConfiguration, Environment environment) throws Exception {
        final ContractRepository contractRepository = new ContractRepository(hibernateBundle.getSessionFactory());
        final ContractService contractService = new ContractService(contractRepository);

        environment.jersey().register(new ContractsController(contractService));
    }
}
