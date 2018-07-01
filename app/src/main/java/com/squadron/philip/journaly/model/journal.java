package com.squadron.philip.journaly.model;

import java.util.Date;

/**
 * Created by philip on 01/07/2018.
 */

public interface journal {
    int getId();
    String getcontent();
    Date getAddedAtDate();
    Date lastModifiedDate();

}
