package mmj.pa;

import org.junit.Test;

import javax.script.*;

import static org.junit.Assert.assertNotNull;

public class MacroManagerTest {

    @Test
    public void javascriptEngineShouldBePresent() {
        ScriptEngineManager factory = new ScriptEngineManager();
        assertNotNull(factory.getEngineByName("js"));
    }
}