// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.api.commands;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.BaseListCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.PhysicalNetworkResponse;
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.response.ListResponse;
import com.cloud.api.response.NiciraNvpDeviceResponse;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.InvalidParameterValueException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.network.NiciraNvpDeviceVO;
import com.cloud.network.element.NiciraNvpElementService;
import com.cloud.utils.exception.CloudRuntimeException;

@APICommand(name = "listNiciraNvpDevices", responseObject=NiciraNvpDeviceResponse.class, description="Lists Nicira NVP devices")
public class ListNiciraNvpDevicesCmd extends BaseListCmd {
    private static final Logger s_logger = Logger.getLogger(ListNiciraNvpDevicesCmd.class.getName());
    private static final String s_name = "listniciranvpdeviceresponse";
    @Inject NiciraNvpElementService _niciraNvpElementService;

   /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.PHYSICAL_NETWORK_ID, type=CommandType.UUID, entityType = PhysicalNetworkResponse.class,
            description="the Physical Network ID")
    private Long physicalNetworkId;

    @Parameter(name=ApiConstants.NICIRA_NVP_DEVICE_ID, type=CommandType.UUID, entityType = NiciraNvpDeviceResponse.class,
            description="nicira nvp device ID")
    private Long niciraNvpDeviceId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getNiciraNvpDeviceId() {
        return niciraNvpDeviceId;
    }

    public Long getPhysicalNetworkId() {
        return physicalNetworkId;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public void execute() throws ResourceUnavailableException, InsufficientCapacityException, ServerApiException, ConcurrentOperationException, ResourceAllocationException {
        try {
            List<NiciraNvpDeviceVO> niciraDevices = _niciraNvpElementService.listNiciraNvpDevices(this);
            ListResponse<NiciraNvpDeviceResponse> response = new ListResponse<NiciraNvpDeviceResponse>();
            List<NiciraNvpDeviceResponse> niciraDevicesResponse = new ArrayList<NiciraNvpDeviceResponse>();

            if (niciraDevices != null && !niciraDevices.isEmpty()) {
                for (NiciraNvpDeviceVO niciraDeviceVO : niciraDevices) {
                    NiciraNvpDeviceResponse niciraDeviceResponse = _niciraNvpElementService.createNiciraNvpDeviceResponse(niciraDeviceVO);
                    niciraDevicesResponse.add(niciraDeviceResponse);
                }
            }

            response.setResponses(niciraDevicesResponse);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        }  catch (InvalidParameterValueException invalidParamExcp) {
            throw new ServerApiException(BaseCmd.PARAM_ERROR, invalidParamExcp.getMessage());
        } catch (CloudRuntimeException runtimeExcp) {
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, runtimeExcp.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return s_name;
    }
    
}
