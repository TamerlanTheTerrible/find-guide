package me.timur.findguide.service.factory;

import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.constant.Command;
import me.timur.findguide.service.CallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 13/03/23.
 */

@Slf4j
@Component
public class CallbackHandlerFactory {

    private final EnumMap<Command, CallbackHandler> map;

    @Autowired
    public CallbackHandlerFactory(List<CallbackHandler> protocolServices) {
        this.map = new EnumMap<>(Command.class);
        protocolServices.forEach(service -> map.put(service.getType(), service));
    }

    public CallbackHandler get(Command command) {
        return map.get(command);
    }

    public CallbackHandler get(String commandStr) {
        return map.get(Command.get(commandStr));
    }
}
