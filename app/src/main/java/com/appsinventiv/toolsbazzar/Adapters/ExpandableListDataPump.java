package com.appsinventiv.toolsbazzar.Adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AliAh on 28/08/2018.
 */

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("Ampara");
        cricket.add("Anuradhapura");
        cricket.add("Badulla");
        cricket.add("Batticaloa");
        cricket.add("Colombo");
        cricket.add("Galle");
        cricket.add("Gampaha");
        cricket.add("Hambantota");
        cricket.add("Jaffna");
        cricket.add("Kalutara");
        cricket.add("Kandy");
        cricket.add("Kegalle");
        cricket.add("Kilinochchi");
        cricket.add("Kurunegala");

        cricket.add("Mannar");
        cricket.add("Matale");
        cricket.add("Matara");
        cricket.add("Moneragala");
        cricket.add("Mullaitivu");
        cricket.add("Nuwara Eliya");
        cricket.add("Polonnaruwa");
        cricket.add("Puttalam");
        cricket.add("Ratnapura");
        cricket.add("Trincomalee");
        cricket.add("Vavuniya");



//        List<String> football = new ArrayList<String>();
//        football.add("Brazil");
//        football.add("Spain");
//        football.add("Germany");
//        football.add("Netherlands");
//        football.add("Italy");
//
//        List<String> basketball = new ArrayList<String>();
//        basketball.add("United States");
//        basketball.add("Spain");
//        basketball.add("Argentina");
//        basketball.add("France");
//        basketball.add("Russia");

        expandableListDetail.put("Sirilanka ", cricket);
//        expandableListDetail.put("FOOTBALL TEAMS", football);
//        expandableListDetail.put("BASKETBALL TEAMS", basketball);
        return expandableListDetail;
    }
}
