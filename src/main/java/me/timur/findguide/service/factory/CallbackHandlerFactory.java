package me.timur.findguide.service.factory;

import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.constant.Command;
import me.timur.findguide.service.UpdateHandlerService;
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

    private final EnumMap<Command, UpdateHandlerService> map;

    @Autowired
    public CallbackHandlerFactory(List<UpdateHandlerService> protocolServices) {
        this.map = new EnumMap<>(Command.class);
        protocolServices.forEach(service -> map.put(service.getType(), service));
    }

    public UpdateHandlerService get(Command command) {
        return map.get(command);
    }

    public UpdateHandlerService get(String commandStr) {
        return map.get(Command.get(commandStr));
    }
}
