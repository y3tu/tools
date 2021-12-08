package com.y3tu.tools.lowcode.common.rest;

import com.y3tu.tools.kit.lang.R;
import com.y3tu.tools.lowcode.base.PageInfo;
import com.y3tu.tools.lowcode.common.entity.domain.DataSource;
import com.y3tu.tools.lowcode.common.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 数据源Controller
 *
 * @author y3tu
 */
@RestController
@RequestMapping("tools-lowcode/dataSource")
public class DataSourceController {

    @Autowired
    DataSourceService dataSourceService;

    @PostMapping("page")
    public R page(@RequestBody PageInfo<DataSource> pageInfo) {
        return R.success(dataSourceService.page(pageInfo));
    }

    @GetMapping("get/{id}")
    public R get(@PathVariable int id) {
        return R.success(dataSourceService.getById(id));
    }

    @GetMapping("getByName/{name}")
    public R getByName(@PathVariable String name) {
        return R.success(dataSourceService.getByName(name));
    }

    @GetMapping("getAll")
    public R getAll() {
        DataSource dataSource = new DataSource();
        return R.success(dataSourceService.getAll(dataSource));
    }

    @PostMapping("create")
    public R create(@RequestBody DataSource dataSource) {
        dataSource.setCreateTime(new Date());
        dataSourceService.create(dataSource);
        return R.success();
    }

    @PostMapping("update")
    public R update(@RequestBody DataSource dataSource) {
        dataSource.setUpdateTime(new Date());
        dataSourceService.update(dataSource);
        return R.success();
    }

    @GetMapping("del/{id}")
    public R del(@PathVariable int id) {
        dataSourceService.delete(id);
        return R.success();
    }

    @GetMapping("testConnect/{id}")
    public R testConnect(@PathVariable int id) {
        DataSource dataSource = (DataSource) dataSourceService.getById(id);
        return R.success(dataSourceService.testConnection(dataSource));
    }
}
