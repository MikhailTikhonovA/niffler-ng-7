package student.jupiter.extension;

import student.data.tmpl.Connections;
import student.jupiter.extension.browser.SuiteExtension;

public class DatabasesExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}
