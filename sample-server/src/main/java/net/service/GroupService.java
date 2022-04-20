package net.service;


import com.google.common.base.Strings;
import net.bean.api.AddMemberModel;
import net.bean.api.CreateGroupModel;
import net.bean.api.base.ResponseModel;
import net.im_server.ImServer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupService extends BaseService {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<String> createGroup(CreateGroupModel model) {
        System.out.println("api create group: " + (model == null ? "null" : model.getGroup_id() == null ? "groupId null" : model.getGroup_id()));
        if (model == null || model.getGroup_id() == null || model.getGroup_id().isEmpty()) {
            return ResponseModel.buildParameterError();
        }
        ImServer.getInstance().getTcpServer().createGroup(model.getGroup_id());
        return ResponseModel.buildOk("创建成功");
    }

    /**
     * 多用户添加
     * @return
     */
    @POST
    @Path("/members/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<String> addMembers(AddMemberModel model) {

        System.out.println("api add member: " + (model == null ? "null" : model.toString()));
        if (model == null || Strings.isNullOrEmpty(model.getGroup_id()) || Strings.isNullOrEmpty(model.getMember_id())) {
            return ResponseModel.buildParameterError();
        }
        String[] member = model.getMember_id().split(",");
        for (String userId : member) {
            ImServer.getInstance().getTcpServer().joinOrRemoveMemberToGroup(model.getGroup_id(), userId, true);
        }
        return ResponseModel.buildOk("加入成功");
    }

}
