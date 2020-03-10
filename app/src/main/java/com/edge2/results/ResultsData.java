package com.edge2.results;

/*
 * Copyright (C) 2020 Ritayan Chakraborty <ritayanout@gmail.com>
 *
 * This file is part of EDGE-new
 *
 * EDGE-new is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EDGE-new is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EDGE-new.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ResultsData {
    // Yes, it physically hurt to write this. Impossible to do it properly on such a short deadline.
    private Map<String, List<ResultsModel>> results;
    private static ResultsData resultsData;

    private ResultsData() {
        results = new HashMap<>();
        List<ResultsModel> list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "PRATHAM CHANDRA\n" +
                "AZQUAN WALI\n" +
                "KR KARAN RAJ\n" +
                "ROHIT PANI\n"));
        list.add(new ResultsModel(2, "", "NISHKARSH KUMAR\n" +
                "SHRESTHA RAJ\n"));
        results.put("Paper-o-vation", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "KR. KARAN RAJ\n" +
                "ROHIT SONI \n" +
                "PRATHAM CHANDRA\n" +
                "AZQUAN WALI\n"));
        list.add(new ResultsModel(2, "", "SONALI\n" +
                "ROZA\n" +
                "AYUSHI\n" +
                "PRITI\n"));
        list.add(new ResultsModel(3, "", "SUDESHNA PAL\n" +
                "DEBARYA PAL\n" +
                "ADITI SAMANTA\n" +
                "SINJINI BISWAS\n   "));
        results.put("Nirmaan", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "DIPANJAN GUCHAIT\n" +
                "NIKHIL AGARWAL\n"));
        list.add(new ResultsModel(2, "", "KUMAR ANKIT\n" +
                "ASHUTOSH PRAKASH\n"));
        list.add(new ResultsModel(3, "", "AYUSHI DEY\n" +
                "ARGHYA BANDYOPADHYAY\n"));
        results.put("Business Quiz", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "TECHNO CRATS", "RISHITA BANIK\n" +
                "ABHIROOP BASAK\n"));
        list.add(new ResultsModel(2, "REPAIRGENT", "ADITYA KUMAR\n"));
        list.add(new ResultsModel(3, "MAID DHUNDO\n", "VANISHA PATWALI\n"));
        results.put("Business model plan", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "ALASKA-2\n", "ANIKET CHAKRABORTY\n" +
                "DWAIPAYAN BAKSHI\n"));
        list.add(new ResultsModel(2, "GO\n", "UTKARSH KUMAR\n" +
                "SHIVAM GUPTA\n" +
                "KOMAL TATER\n" +
                "VISHAL SHAW\n"));
        list.add(new ResultsModel(3, "BONG AZTECHS\n", "SANKHA CHAKRABORTY\n" +
                "SARTHAK DUTTA\n" +
                "SOUVIK CHATTARAJ\n" +
                "PRITHIS GANGULY\n"));
        results.put("Mekanix", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "ANIF HUSSAIN\n" +
                "MD AFTAB ALAM\n" +
                "DEBOJYOTI PRAMANIK\n"));
        list.add(new ResultsModel(2, "", "NILADRI DEY\n" +
                "PRASHANT KR. SHAW\n"));
        list.add(new ResultsModel(3, "", "ISHIKA MONDAL\n" +
                "ADITI SARAWGI\n" +
                "AWADHESH\n"));
        results.put("Electronically Yours", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "FOR THE MOTION\n", "ATUL KUMAR\n"));
        list.add(new ResultsModel(2, "AGAINST THE MOTION\n", "SACHIN KUMAR MISHRA\n"));
        results.put("Youth Parliament", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "WINNERS EVERYWHERE\n", "ARKADIPTA PAUL\n" +
                "AYANDIP GUHA\n" +
                "SAYANTA KUNDU\n"));
        list.add(new ResultsModel(2, "HONOURABLE MENTIONS\n", "SUVRADEEP RAHA\n" +
                "ABHISEK CHOWDHURY\n" +
                "SHAMBO CHATTERJEE\n"));
        list.add(new ResultsModel(3, "BOYS IN AREA 51\n", "ANIKET CHAKRABORT\n" +
                "SHAMIK BANERJEE\n" +
                "SOUMYABRATA CHAKRABORTY\n"));
        results.put("Quiz", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "SOUBHAGGYA MALLICK\n"));
        list.add(new ResultsModel(2, "", "AVISHEK PRASAD\n"));
        list.add(new ResultsModel(3, "", "ANMOL PRAKASH\n"));
        results.put("Shoot-M-Up", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "SUHRID PAL\n"));
        list.add(new ResultsModel(2, "", "BARISH RAHAMAN\n"));
        list.add(new ResultsModel(3, "", "SUBRAJIT ROY\n"));
        results.put("Crumbs", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "SREEPARNO DHAR\n"));
        list.add(new ResultsModel(2, "", "AVISHEK PRASAD\n"));
        results.put("Photo Story", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "PRATYUSH BISWAKARMA\n" +
                "RAKSHA AGARWAL\n"));
        list.add(new ResultsModel(2, "", "VISHAL SINGH\n" +
                "ASHUTOSH SINGH\n"));
        list.add(new ResultsModel(3, "", "SOUMITA KAR\n" +
                "KUMARI SUMIRAN GUPTA\n" +
                "SHALINI KUMARI\n"));
        results.put("2 Minutes To Sell", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "SOUMI DOLUI\n" +
                "DEBOLINA SAHA\n" +
                "SHALABDI MUNSHI\n"));
        list.add(new ResultsModel(2, "", "SNEHA VERMA \n" +
                "ANAMITRA GHOSH\n" +
                "MANISHA KUMARI\n"));
        list.add(new ResultsModel(3, "TEAM 1", "RISHITA BANIK\n" +
                "ABHIROOP BASAK\n"));
        list.add(new ResultsModel(3, "TEAM 2", "MD.SHAQKID\n" +
                "MEGHA BURUWAL\n" +
                "DEBARSHI BHATTACHARYA\n"));
        results.put("Process Puzzle", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "ALANKRITI MALLIK\n" +
                "VISHWA RAJ SHAH\n"));
        list.add(new ResultsModel(2, "", "VISHAL SHAW\n" +
                "SHIVAM GUPTA \n"));
        list.add(new ResultsModel(3, "", "PRERNA CHOWDHURY\n" +
                "ABANTIKA CHAIL \n" +
                "ADRIJA BHOWMIK\n"));
        results.put("Xquizit", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "DEBARYA PAL\n"));
        list.add(new ResultsModel(2, "", "SOUMI BISWAS\n" +
                "SAANTANI BISWAS\n"));
        list.add(new ResultsModel(3, "", "RETANKAR GHOSH\n"));
        results.put("Poster Presentation", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "SABARNA SEN\n" +
                "RUCHIDRITA DAS\n" +
                "VISHAL SINGH\n"));
        list.add(new ResultsModel(2, "", "VISHWA RAJ SHAH\n" +
                "MEGHA KONAR\n"));
        list.add(new ResultsModel(3, "", "KAUSTAV DE\n" +
                "MAHIM MAJHEL\n" +
                "ABHINABA PAUL\n"));
        results.put("Food Relay", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "ADNAN AHMED HUSSAIN\n" +
                "ARCHISMAN DEY\n"));
        list.add(new ResultsModel(2, "", "SAGNIK SAHA\n"));
        list.add(new ResultsModel(3, "", "ABANTIKA CHAIL\n" +
                "PRERONA CHOWDHURY\n"));
        results.put("Animate", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "ROBO SAPIENZ OMEGA\n", "KAUSHAL KISHORE\n" +
                "ANIKET KUMAR\n" +
                "ROHIT KR SINGH\n" +
                "RAJEEV TIWARI\n"));
        list.add(new ResultsModel(2, "PATRONUS\n", "RICHA CHOWDHARY\n" +
                "ARJU SINGH\n"));
        list.add(new ResultsModel(3, "ROBO TYPHON\n", "Souvik Saha\n" +
                "Rajsekhar Paul\n" +
                "Sayan Roy\n" +
                "Supriya Singha\n"));
        results.put("Robo Soccer", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "DROID PANTHER\n", "TRINA RAUT\n" +
                "SURYANATH\n" +
                "PRUSHA SENGUPTA\n" +
                "WRIT PAL\n"));
        list.add(new ResultsModel(2, "ROBO SAPIENZ DELTA\n", "ABHISHEK MAHESWARI\n"));
        list.add(new ResultsModel(3, "ROBO SAPIENZ OMEGA\n", "KRISHNA SHARMA\n" +
                "ANIKET KUMAR\n"));
        results.put("Rumble", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "ROBO SPAARX\n", "RITAM ROY\n" +
                "SHOUNAK BASU\n" +
                "AMAN KR. ROY\n" +
                "PRATAY GHOSH\n"));
        list.add(new ResultsModel(2, "LOLITA\n", "KUNDAN KUMAR\n" +
                "ARPIT MAHI\n"));
        list.add(new ResultsModel(3, "PATRONUS\n", "ARJU SINGH\n" +
                "RISHA CHOWDHARY\n"));
        results.put("Robo Race", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "EMBARRESMENT\n", "DEBADITYA MUKHERJEE\n" +
                "RAJDIP SAHA\n" +
                "ANUSHKA DAS\n" +
                "ANIRUDDHA SAHA\n"));
        list.add(new ResultsModel(2, "SNICRUX\n", "ANIMESH ARYA\n" +
                "HEMANT KUMAR\n" +
                "ADITYA NAVYA SHUKLA\n" +
                "MD. RIYAN ALAM\n"));
        results.put("Micro-Machina", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "SNICRUX\n", "ANIMESH ARYA\n" +
                "HEMANT KUMAR\n" +
                "ADITYA NAVYA SHUKLA\n" +
                "MD. RIYAN ALAM\n"));
        list.add(new ResultsModel(2, "EMBARRESMENT\n", "DEBADITYA MUKHERJEE\n" +
                "RAJDIP SAHA\n" +
                "ANUSHKA DAS\n" +
                "ANIRUDDHA SAHA\n"));
        list.add(new ResultsModel(3, "ROBOKON\n", "SREERUPA SENGUPTA\n" +
                "PRAYASH KUMAR SAHA\n" +
                "ANKAN NATH\n"));
        results.put("Block City", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "SNICRUX\n", "ANIMESH ARYA\n" +
                "HEMANT KUMAR\n" +
                "ADITYA NAVYA SHUKLA\n" +
                "MD. RIYAN ALAM\n"));
        list.add(new ResultsModel(2, "ROBOKON\n", "SREERUPA SENGUPTA\n" +
                "PRAYASH KUMAR SAHA\n" +
                "ANKAN NATH\n"));
        list.add(new ResultsModel(3, "ASYMPTOTE\n", "PRIYANKA KUMARI\n"));
        results.put("Track Hunter", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "SSID\n", "Priyankar Mitra \n" +
                "Subham kumar \n"));
        list.add(new ResultsModel(2, "Runtime Terror \n", "Ankit Poddar \n" +
                "Ayam Dayal \n"));
        list.add(new ResultsModel(3, "300IQ\n", "Aditya \n" +
                "Rahul Kumar \n"));
        results.put("Web Dev", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "The Incredibles\n", "Soumit Saha \n" +
                "Subham Bhattacharya \n"));
        list.add(new ResultsModel(2, "Double A \n", "Aditi Sarawgi \n" +
                "Avdesh \n"));
        list.add(new ResultsModel(3, "Anshuman_D\n", "Anshuman  Dubey \n"));
        results.put("Crypto Quest", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "Runtime Terror \n", "Ankit Poddar \n" +
                "Dipanjan \n"));
        list.add(new ResultsModel(2, "Flawless_a\n", "Amartya Basu \n" +
                "Rajarshi \n" +
                "Sanbid Roy Chowdhury \n"));
        results.put("Flawless", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "Anshuman Dubey \n"));
        list.add(new ResultsModel(2, "", "Soumit Saha \n"));
        list.add(new ResultsModel(3, "", "Ayush Jain\n"));
        results.put("Bughunt", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "SAMRAT DAS\n"));
        list.add(new ResultsModel(2, "", "SUMAN DEBNATH \n"));
        list.add(new ResultsModel(3, "", "ADARSH BAPSY\n"));
        results.put("Need For Speed", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "RHID BHOWMICK\n"));
        list.add(new ResultsModel(2, "", "ROHAN CHAKRABORTY\n"));
        list.add(new ResultsModel(3, "", "SUBHRA SANKAR ROY\n"));
        results.put("FIFA 11", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(2, "", "ARCHISMAN DEY\n" +
                "BIKRAM KARMAKAR\n" +
                "DEBADITTYA MUKHERJEE\n" +
                "ABDUL BAQUIUR\n" +
                "ARGHYA DATTA\n"));
        results.put("CS 1.6", list);
        list = new LinkedList<>();

        list.add(new ResultsModel(1, "", "REETAM DAS\n" +
                "SUBHAJIT KONAR\n"));
        list.add(new ResultsModel(2, "", "ARNAB DHARA\n" +
                "ARYAN MAJUMDAR\n"));
        list.add(new ResultsModel(3, "", "ANIKET DEY\n" +
                "ANIKET ROUT\n"));
        results.put("PUBG", list);
    }

    static List<ResultsModel> getResult(String event) {
        if (resultsData == null)
            resultsData = new ResultsData();

        return resultsData.results.get(event);
    }
}
