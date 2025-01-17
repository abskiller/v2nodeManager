package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.jcraft.jsch.JSchException;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.ShellUtil;
import com.ruoyi.system.domain.V2Dns;
import com.ruoyi.system.domain.V2Node;
import com.ruoyi.system.service.ConfigService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.IV2DnsService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.V2ServerMapper;
import com.ruoyi.system.domain.V2Server;
import com.ruoyi.system.service.IV2ServerService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-05-10
 */
@Service
public class V2ServerServiceImpl implements IV2ServerService 
{
    @Autowired
    private V2ServerMapper v2ServerMapper;

    @Autowired
    private ShellUtil shellUtil;

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private IV2DnsService iv2DnsService;

    @Autowired
    private ConfigService configService;

//    ConcurrentHashMap<String,ShellUtil> shellMap = new ConcurrentHashMap<>();

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public V2Server selectV2ServerById(Long id)
    {
        return v2ServerMapper.selectV2ServerById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param v2Server 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<V2Server> selectV2ServerList(V2Server v2Server)
    {
        return v2ServerMapper.selectV2ServerList(v2Server);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param v2Server 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertV2Server(V2Server v2Server)
    {
        return v2ServerMapper.insertV2Server(v2Server);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param v2Server 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateV2Server(V2Server v2Server)
    {
        return v2ServerMapper.updateV2Server(v2Server);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteV2ServerByIds(Long[] ids)
    {
        return v2ServerMapper.deleteV2ServerByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteV2ServerById(Long id)
    {
        return v2ServerMapper.deleteV2ServerById(id);
    }

    @Override
    public AjaxResult checkInstallStatus(Long id) {
        V2Server v2Server = v2ServerMapper.selectV2ServerById(id);
        ShellUtil shellone = shellUtil.getOne();
        try {

            shellone.init(v2Server.getIp(), Integer.valueOf(v2Server.getPort()) ,v2Server.getUser(), v2Server.getPasswd());
            String execCmd = shellone.execCmdAndClose("systemctl status XrayR");
            if(execCmd.contains("Loaded: loaded")) {
                return AjaxResult.success("服务已安装");
            } else {
                return AjaxResult.error("服务未安装");
            }
        } catch (JSchException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AjaxResult installStatus(Long id) {

        V2Server v2Server = v2ServerMapper.selectV2ServerById(id);
//        if ((shellMap.contains(v2Server.getIp()))) {
//
//        }
        ShellUtil shellone = shellUtil.getOne();
        String xrayRScript = sysConfigService.selectConfigByKey("XrayRScript");
        return null;

//        try {
//            shellone.init(v2Server.getIp(), Integer.valueOf(v2Server.getPort()) ,v2Server.getUser(), v2Server.getPasswd());
//            shellone.execCmdAndClose()
//            if(execCmd.contains("Loaded: loaded")) {
//                return AjaxResult.error("服务已安装");
//            } else {
//                String execCmd1 = shellone.execCmd("bash " + xrayRScript);
//                if(execCmd1.contains("success")) {
//                    return AjaxResult.success("安装成功");
//                } else {
//                    return AjaxResult.error("安装失败");
//                }
//            }
//        } catch (JSchException e) {
//            return AjaxResult.error(e.getMessage());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            shellone.close();
//        }
    }

    @Override
    public AjaxResult execFileReplace(Long id, Long templateId) {

        return null;
    }

    @Override
    public V2Server selectV2ServerByIp(String ip) {
        return v2ServerMapper.selectV2ServerByIp(ip);
    }

    @Override
    @DataSource(value = DataSourceType.v2board)
    public V2Node selectV2NodeInfoByNodeId(Long nodeId) {
        return v2ServerMapper.selectV2NodeInfoByNodeId(nodeId);
    }

    @Override
    @DataSource(value = DataSourceType.v2board)
    public void updateV2Node(Long nodeId, String content) {
        v2ServerMapper.updateV2Node(nodeId, content);
    }

    @Override
    @DataSource(value = DataSourceType.v2board)
    public List<V2Node> selectV2NodeInfoByNodeIds(List<Long> nodeids) {
        if (CollectionUtils.isEmpty(nodeids)) {
            return new ArrayList<>();
        }
       return v2ServerMapper.selectV2NodeInfoByNodeIds(nodeids);
    }

    @Override
    public AjaxResult quickHostReplace(V2Dns data) {
        Long serverId = data.getServerId();
        data.setType("A");
        data.setName(data.getName() +"."+ data.getZoneName());
        V2Server v2Server = v2ServerMapper.selectV2ServerById(serverId);
        data.setContent(v2Server.getIp());
        data.setTtl(3600L);
        AjaxResult v2Dns = iv2DnsService.createV2Dns(data);
        if (v2Dns.isSuccess()) {
            V2Dns dnsByName = iv2DnsService.selectV2DnsByName(data.getName());
            AjaxResult replaced = configService.replaceConfig(dnsByName.getId(), data.getTemplateId());
            return replaced;
        }else {
            return v2Dns;
        }
    }
}
