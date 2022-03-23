package guiceModule;

import com.google.inject.AbstractModule;
import menu.Menu;
import menu.StartMenu;

public class HexagonGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Menu.class).to(StartMenu.class);
    }
}
