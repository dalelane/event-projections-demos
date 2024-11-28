/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.dalelane.kafkaprojections.data.loosehanger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Events on the "DOOR.BADGEIN" topic are JSON strings that look like this:
 *
 *  {
 *      "recordid": "e85bde81-b883-4321-98a3-e8f20a73c344",
 *      "door": "H-0-17",
 *      "employee": "micaela.shanahan",
 *      "badgetime": "2024-11-26 17:51:11.988"
 *  }
 */
public class DoorBadgeIn {

    @SerializedName("recordid")
    @Expose
    private String recordid;

    @SerializedName("door")
    @Expose
    private String door;

    @SerializedName("employee")
    @Expose
    private String employee;

    @SerializedName("badgetime")
    @Expose
    private String badgetime;


    public String getRecordId() {
        return recordid;
    }

    public String getDoorId() {
        return door;
    }

    public String getEmployee() {
        return employee;
    }

    public String getBadgeTime() {
        return badgetime;
    }
}
