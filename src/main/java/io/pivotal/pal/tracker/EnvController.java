package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    String PORT;
    String MEMORY_LIMIT;
    String CF_INSTANCE_INDEX;
    String CF_INSTANCE_ADDR;

    @GetMapping("/env")
    public Map<String, String> getEnv() throws Exception{

        Map<String, String> map = new HashMap<>();

        map.put("PORT", PORT);
        map.put("MEMORY_LIMIT", MEMORY_LIMIT);
        map.put("CF_INSTANCE_INDEX", CF_INSTANCE_INDEX);
        map.put("CF_INSTANCE_ADDR", CF_INSTANCE_ADDR);

        return map;
    }

    public EnvController(@Value("${port:NOT SET}") String PORT, @Value("${memory.limit:NOT SET}") String MEMORY_LIMIT, @Value("${cf.instance.index:NOT SET}") String CF_INSTANCE_INDEX, @Value("${cf.instance.addr:NOT SET}") String CF_INSTANCE_ADDR) {
        this.PORT = PORT;
        this.MEMORY_LIMIT = MEMORY_LIMIT;
        this.CF_INSTANCE_INDEX = CF_INSTANCE_INDEX;
        this.CF_INSTANCE_ADDR = CF_INSTANCE_ADDR;
    }
}
