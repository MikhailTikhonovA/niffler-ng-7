package config;

public interface Config {

    static Config getInstance(){
        return LocalConfig.INSTANCE;
    }

    String frontUrl();

    String spendUrl();
}