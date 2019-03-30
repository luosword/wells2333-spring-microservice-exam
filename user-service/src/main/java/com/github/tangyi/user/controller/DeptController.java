package com.github.tangyi.user.controller;

import cn.hutool.core.collection.CollUtil;
import com.github.tangyi.common.core.model.ResponseBean;
import com.github.tangyi.common.core.utils.SysUtil;
import com.github.tangyi.common.core.utils.TreeUtil;
import com.github.tangyi.common.core.vo.DeptVo;
import com.github.tangyi.common.core.web.BaseController;
import com.github.tangyi.common.log.annotation.Log;
import com.github.tangyi.common.security.utils.SecurityUtil;
import com.github.tangyi.user.api.dto.DeptDto;
import com.github.tangyi.user.api.module.Dept;
import com.github.tangyi.user.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 部门controller
 *
 * @author tangyi
 * @date 2018/8/26 0026 22:49
 */
@Api("部门信息管理")
@RestController
@RequestMapping("/v1/dept")
public class DeptController extends BaseController {

    @Autowired
    private DeptService deptService;

    /**
     * 查询树形部门集合
     *
     * @return List
     * @author tangyi
     * @date 2018/10/25 12:57
     */
    @GetMapping(value = "/depts")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = "获取部门列表")
    public List<DeptDto> depts() {
        // 查询所有部门
        Set<Dept> deptSet = new HashSet<Dept>(deptService.findList(new Dept()));
        List<DeptDto> deptTreeList = new ArrayList<DeptDto>();
        deptSet.forEach(dept -> deptTreeList.add(new DeptDto(dept)));
        // 排序
        CollUtil.sort(deptTreeList, Comparator.comparingInt(DeptDto::getSort));
        return TreeUtil.buildTree(deptTreeList, "-1");
    }

    /**
     * 根据id获取部门
     *
     * @param id id
     * @return Dept
     * @author tangyi
     * @date 2018/8/28 10:11
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = "获取部门信息", notes = "根据部门id获取部门详细信息")
    @ApiImplicitParam(name = "id", value = "部门ID", required = true, dataType = "String", paramType = "path")
    public Dept get(@PathVariable String id) {
        Dept dept = new Dept();
        dept.setId(id);
        return deptService.get(dept);
    }

    /**
     * 新增部门
     *
     * @param dept dept
     * @return ResponseBean
     * @author tangyi
     * @date 2018/8/28 10:15
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = "创建部门", notes = "创建部门")
    @ApiImplicitParam(name = "dept", value = "部门实体", required = true, dataType = "Dept")
    @Log("新增部门")
    public ResponseBean<Boolean> add(@RequestBody Dept dept) {
        dept.setCommonValue(SecurityUtil.getCurrentUsername(), SysUtil.getSysCode());
        return new ResponseBean<>(deptService.insert(dept) > 0);
    }

    /**
     * 删除部门
     *
     * @param id id
     * @return ResponseBean
     * @author tangyi
     * @date 2018/8/28 10:16
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = "删除部门", notes = "根据ID删除部门")
    @ApiImplicitParam(name = "id", value = "部门ID", required = true, paramType = "path")
    @Log("删除部门")
    public ResponseBean<Boolean> delete(@PathVariable String id) {
        Dept dept = new Dept();
        dept.setId(id);
        dept.setCommonValue(SecurityUtil.getCurrentUsername(), SysUtil.getSysCode());
        return new ResponseBean<>(deptService.delete(dept) > 0);
    }

    /**
     * 更新部门
     *
     * @param dept dept
     * @return ResponseBean
     * @author tangyi
     * @date 2018/8/28 10:22
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = "更新部门信息", notes = "根据部门id更新部门的基本信息")
    @ApiImplicitParam(name = "dept", value = "部门实体", required = true, dataType = "Dept")
    @Log("更新部门")
    public ResponseBean<Boolean> update(@RequestBody Dept dept) {
        dept.setCommonValue(SecurityUtil.getCurrentUsername(), SysUtil.getSysCode());
        return new ResponseBean<>(deptService.update(dept) > 0);
    }

    /**
     * 根据ID查询
     *
     * @param deptVo deptVo
     * @return ResponseBean
     * @author tangyi
     * @date 2018/12/31 22:13
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = "批量查询部门信息", notes = "根据Ids批量查询信息")
    @ApiImplicitParam(name = "deptVo", value = "部门实体", required = true, dataType = "DeptVo")
    public ResponseBean<List<DeptVo>> findById(@RequestBody DeptVo deptVo) {
        ResponseBean<List<DeptVo>> returnT = null;
        Dept dept = new Dept();
        dept.setIds(deptVo.getIds());
        List<Dept> depts = deptService.findListById(dept);
        if (CollectionUtils.isNotEmpty(depts)) {
            List<DeptVo> deptVoList = new ArrayList<>();
            depts.forEach(tempDept -> {
                DeptVo tempDeptVo = new DeptVo();
                BeanUtils.copyProperties(tempDept, tempDeptVo);
                deptVoList.add(tempDeptVo);
            });
            returnT = new ResponseBean<>(deptVoList);
        }
        return returnT;
    }
}