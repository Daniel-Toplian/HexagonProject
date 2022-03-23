package guiceModule;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import menu.Menu;
import menu.StartMenu;

public class HexagonGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(GraphicalComponentFactory.class));
        install(new FactoryModuleBuilder().build(GameBoardFactory.class));
        bind(Menu.class).to(StartMenu.class);
    }
}
