package com.snow.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.system.mapper.SysMenuMapper;
import com.snow.system.mapper.SysRoleMapper;
import com.snow.system.mapper.SysRoleMenuMapper;
import com.snow.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.snow.common.constant.Constants;
import com.snow.common.constant.UserConstants;
import com.snow.common.core.domain.TreeSelect;
import com.snow.common.core.domain.entity.SysMenu;
import com.snow.common.core.domain.entity.SysUser;
import com.snow.common.utils.SecurityUtils;
import com.snow.common.utils.StringUtils;
import com.snow.system.domain.vo.MetaVo;
import com.snow.system.domain.vo.RouterVo;
import reactor.core.publisher.Mono;

/**
 * 菜单 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysMenuServiceImpl
	extends BaseServiceImpl<SysMenu, Long, SysMenuMapper>
	implements ISysMenuService {
	public static final String PREMISSION_STRING = "perms[\"{0}\"]";

	@Autowired
	private SysRoleMapper roleMapper;

	@Autowired
	private SysRoleMenuMapper roleMenuMapper;

	/**
	 * 根据用户查询系统菜单列表
	 *
	 * @param userId 用户ID
	 * @return 菜单列表
	 */
	@Override
	public Mono<List<SysMenu>> selectMenuList(Long userId) {
		return selectMenuList(new SysMenu(), userId);
	}

	/**
	 * 查询系统菜单列表
	 *
	 * @param menu 菜单信息
	 * @return 菜单列表
	 */
	@Override
	public Mono<List<SysMenu>> selectMenuList(SysMenu menu, Long userId) {
		// 管理员显示所有菜单信息
		if (SysUser.isAdmin(userId)) {
			return super.selectList(menu);
		} else {
			menu.getParams().put("userId", userId);
			return mapper.selectMenuListByUserId(menu).collectList();
		}

	}

	/**
	 * 根据用户ID查询权限
	 *
	 * @param userId 用户ID
	 * @return 权限列表
	 */
	@Override
	public Mono<Set<String>> selectMenuPermsByUserId(Long userId) {
		return mapper.selectMenuPermsByUserId(userId)
			.collectList()
			.map(perms -> {
				Set<String> permsSet = new HashSet<>();
				for (String perm : perms) {
					if (StringUtils.isNotEmpty(perm)) {
						permsSet.addAll(Arrays.asList(perm.trim().split(",")));
					}
				}
				return permsSet;
			});
	}

	/**
	 * 根据用户ID查询菜单
	 *
	 * @param userId 用户名称
	 * @return 菜单列表
	 */
	@Override
	public Mono<List<SysMenu>> selectMenuTreeByUserId(Long userId) {
		if (SecurityUtils.isAdmin(userId)) {
			return mapper.selectMenuTreeAll().collectList().map(menus -> {
				return getChildPerms(menus, 0);
			});
		} else {
			return mapper.selectMenuTreeByUserId(userId).collectList().map(menus -> {
				return getChildPerms(menus, 0);
			});
		}
	}

	/**
	 * 根据角色ID查询菜单树信息
	 *
	 * @param roleId 角色ID
	 * @return 选中菜单列表
	 */
	@Override
	public Mono<List<Long>> selectMenuListByRoleId(Long roleId) {
		return roleMapper.selectById(roleId).flatMap(role -> {
			return mapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly())
				.collectList();
		});
	}

	/**
	 * 构建前端路由所需要的菜单
	 *
	 * @param menus 菜单列表
	 * @return 路由列表
	 */
	@Override
	public List<RouterVo> buildMenus(List<SysMenu> menus) {
		List<RouterVo> routers = new LinkedList<>();
		for (SysMenu menu : menus) {
			RouterVo router = new RouterVo();
			router.setHidden("1".equals(menu.getVisible()));
			router.setName(getRouteName(menu));
			router.setPath(getRouterPath(menu));
			router.setComponent(getComponent(menu));
			router.setQuery(menu.getQuery());
			router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
			List<SysMenu> cMenus = menu.getChildren();
			if (!cMenus.isEmpty() && cMenus.size() > 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
				router.setAlwaysShow(true);
				router.setRedirect("noRedirect");
				router.setChildren(buildMenus(cMenus));
			} else if (isMenuFrame(menu)) {
				router.setMeta(null);
				List<RouterVo> childrenList = new ArrayList<>();
				RouterVo children = new RouterVo();
				children.setPath(menu.getPath());
				children.setComponent(menu.getComponent());
				children.setName(StringUtils.capitalize(menu.getPath()));
				children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
				children.setQuery(menu.getQuery());
				childrenList.add(children);
				router.setChildren(childrenList);
			} else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
				router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
				router.setPath("/");
				List<RouterVo> childrenList = new ArrayList<>();
				RouterVo children = new RouterVo();
				String routerPath = innerLinkReplaceEach(menu.getPath());
				children.setPath(routerPath);
				children.setComponent(UserConstants.INNER_LINK);
				children.setName(StringUtils.capitalize(routerPath));
				children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
				childrenList.add(children);
				router.setChildren(childrenList);
			}
			routers.add(router);
		}
		return routers;
	}

	/**
	 * 构建前端所需要树结构
	 *
	 * @param menus 菜单列表
	 * @return 树结构列表
	 */
	@Override
	public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
		List<SysMenu> returnList = new ArrayList<>();
		List<Long> tempList = new ArrayList<>();
		for (SysMenu dept : menus) {
			tempList.add(dept.getMenuId());
		}
		for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
			SysMenu menu = (SysMenu) iterator.next();
			// 如果是顶级节点, 遍历该父节点的所有子节点
			if (!tempList.contains(menu.getParentId())) {
				recursionFn(menus, menu);
				returnList.add(menu);
			}
		}
		if (returnList.isEmpty()) {
			returnList = menus;
		}
		return returnList;
	}

	/**
	 * 构建前端所需要下拉树结构
	 *
	 * @param menus 菜单列表
	 * @return 下拉树结构列表
	 */
	@Override
	public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
		List<SysMenu> menuTrees = buildMenuTree(menus);
		return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
	}

	/**
	 * 是否存在菜单子节点
	 *
	 * @param menuId 菜单ID
	 * @return 结果
	 */
	@Override
	public Mono<Boolean> hasChildByMenuId(Long menuId) {
		return mapper.hasChildByMenuId(menuId).map(result -> {
			return result > 0 ? true : false;
		});
	}

	/**
	 * 查询菜单使用数量
	 *
	 * @param menuId 菜单ID
	 * @return 结果
	 */
	@Override
	public Mono<Boolean> checkMenuExistRole(Long menuId) {
		return roleMenuMapper.checkMenuExistRole(menuId).map(result -> {
			return result > 0 ? true : false;
		});
	}

	/**
	 * 校验菜单名称是否唯一
	 *
	 * @param menu 菜单信息
	 * @return 结果
	 */
	@Override
	public Mono<String> checkMenuNameUnique(SysMenu menu) {
		Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
		return mapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId()
		).map(info -> {
			if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
				return UserConstants.NOT_UNIQUE;
			}
			return UserConstants.UNIQUE;
		});
	}

	/**
	 * 获取路由名称
	 *
	 * @param menu 菜单信息
	 * @return 路由名称
	 */
	public String getRouteName(SysMenu menu) {
		String routerName = StringUtils.capitalize(menu.getPath());
		// 非外链并且是一级目录（类型为目录）
		if (isMenuFrame(menu)) {
			routerName = StringUtils.EMPTY;
		}
		return routerName;
	}

	/**
	 * 获取路由地址
	 *
	 * @param menu 菜单信息
	 * @return 路由地址
	 */
	public String getRouterPath(SysMenu menu) {
		String routerPath = menu.getPath();
		// 内链打开外网方式
		if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
			routerPath = innerLinkReplaceEach(routerPath);
		}
		// 非外链并且是一级目录（类型为目录）
		if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
			&& UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
			routerPath = "/" + menu.getPath();
		}
		// 非外链并且是一级目录（类型为菜单）
		else if (isMenuFrame(menu)) {
			routerPath = "/";
		}
		return routerPath;
	}

	/**
	 * 获取组件信息
	 *
	 * @param menu 菜单信息
	 * @return 组件信息
	 */
	public String getComponent(SysMenu menu) {
		String component = UserConstants.LAYOUT;
		if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
			component = menu.getComponent();
		} else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
			component = UserConstants.INNER_LINK;
		} else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
			component = UserConstants.PARENT_VIEW;
		}
		return component;
	}

	/**
	 * 是否为菜单内部跳转
	 *
	 * @param menu 菜单信息
	 * @return 结果
	 */
	public boolean isMenuFrame(SysMenu menu) {
		return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
			&& menu.getIsFrame().equals(UserConstants.NO_FRAME);
	}

	/**
	 * 是否为内链组件
	 *
	 * @param menu 菜单信息
	 * @return 结果
	 */
	public boolean isInnerLink(SysMenu menu) {
		return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
	}

	/**
	 * 是否为parent_view组件
	 *
	 * @param menu 菜单信息
	 * @return 结果
	 */
	public boolean isParentView(SysMenu menu) {
		return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
	}

	/**
	 * 根据父节点的ID获取所有子节点
	 *
	 * @param list     分类表
	 * @param parentId 传入的父节点ID
	 * @return String
	 */
	public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
		List<SysMenu> returnList = new ArrayList<>();
		for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext(); ) {
			SysMenu t = (SysMenu) iterator.next();
			// 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
			if (t.getParentId() == parentId) {
				recursionFn(list, t);
				returnList.add(t);
			}
		}
		return returnList;
	}

	/**
	 * 递归列表
	 */
	private void recursionFn(List<SysMenu> list, SysMenu t) {
		// 得到子节点列表
		List<SysMenu> childList = getChildList(list, t);
		t.setChildren(childList);
		for (SysMenu tChild : childList) {
			if (hasChild(list, tChild)) {
				recursionFn(list, tChild);
			}
		}
	}

	/**
	 * 得到子节点列表
	 */
	private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
		List<SysMenu> tlist = new ArrayList<>();
		Iterator<SysMenu> it = list.iterator();
		while (it.hasNext()) {
			SysMenu n = (SysMenu) it.next();
			if (n.getParentId().longValue() == t.getMenuId().longValue()) {
				tlist.add(n);
			}
		}
		return tlist;
	}

	/**
	 * 判断是否有子节点
	 */
	private boolean hasChild(List<SysMenu> list, SysMenu t) {
		return getChildList(list, t).size() > 0;
	}

	/**
	 * 内链域名特殊字符替换
	 */
	public String innerLinkReplaceEach(String path) {
		return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS},
			new String[]{"", ""});
	}
}
