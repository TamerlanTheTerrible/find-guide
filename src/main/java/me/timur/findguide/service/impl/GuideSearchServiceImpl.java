package me.timur.findguide.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.timur.findguide.constant.CallbackPrefix;
import me.timur.findguide.service.GuideSearchService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 14/04/23.
 */

@Slf4j
@Service
public class GuideSearchServiceImpl implements GuideSearchService {

    private final static String callbackPrefix = CallbackPrefix.GUIDE_PARAMS.name();

//    public List<SendMessage> search()
}
