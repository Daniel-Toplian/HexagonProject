import com.google.inject.Guice;
import com.google.inject.Injector;
import guiceModule.HexagonGuiceModule;
import menu.Menu;

public class HexagonGame {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new HexagonGuiceModule());
        Menu gameMenu = injector.getInstance(Menu.class);
        gameMenu.start(0);
    }
}
