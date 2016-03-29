package ctrip.android.hotel.filter.widget;
/*package ctrip.android.hotel.filter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ctrip.android.view.hotel.R;
import ctrip.android.view.hotel.constant.HotelConstant;
import ctrip.android.view.hotel.inland.list2.TreeNode.ClickHandler;
import ctrip.android.view.hotel.inland.list2.TreeNode.LazyLoader;
import ctrip.android.view.hotel.inland.list2.TreeNode.ValueInflater;
import ctrip.base.logical.component.widget.CtripTitleView;
import ctrip.base.logical.component.widget.CtripTitleView.OnTitleClickListener;
import ctrip.base.logical.util.CtripActionLogUtil;
import ctrip.business.database.HotelDBUtils;
import ctrip.business.viewmodel.CityModel;
import ctrip.business.viewmodel.FilterSimpleDataModel;
import ctrip.foundation.util.DeviceUtil;
import ctrip.foundation.util.StringUtil;
import ctrip.model.HotelSeniorFilterDataModel;
import ctrip.model.HotelSeniorFilterModel;
import ctrip.sender.SenderResultModel;
import ctrip.viewcache.hotel.viewmodel.KeywordTabViewModel;
import ctrip.viewcache.widget.ScreeningCacheBean;
import ctrip.viewcache.widget.ScreeningCacheBean.HotelDataTypeEnum;

public abstract class TreeFragment extends BaseFragment {

	public static final int Business_Inland = 1;
	public static final int Business_Overseas = 2;
	public static final int Business_WiseHotel = 3;
	public static final int Business_GroupHotel = 4;

	protected TreeView mTreeView;
	protected TreeNode mRootNode;

	protected CityModel mCityModel;
	protected HotelSeniorFilterModel mFilterModel;
	protected FilterModifier mFilterModifier;
	protected int mBusinessType;
	protected HotelDataTypeEnum mHotelDataTypeEnum;
	protected String mCheckInDate;
	protected String mCheckOutDate;
	protected ScreeningCacheBean mScreeningCacheBean = ScreeningCacheBean
			.getInstance();
	protected TreeNode mActiveNode;
	protected int mItemHeightBig;
	protected int mItemHeightSmall;

	protected CtripTitleView mTitleView;

	protected Button mSubmit;

	protected List<FilterSimpleDataModel> mOldFilterSimpleDataModels = new ArrayList<FilterSimpleDataModel>();

	protected HashMap<String, String> mStaticActionCode = new HashMap<String, String>();
	protected HashMap<String, Integer> mChoiceCount = new HashMap<String, Integer>();
	protected HashMap<String, Boolean> mAllowNoSelection = new HashMap<String, Boolean>();

	protected void initActionCodeData() {
	}

	protected abstract void initChoiceCount();

	protected void initAllowNoSelection() {
	};

	protected boolean isValidateDataName(String name) {
		return !TextUtils.isEmpty(name)
				&& !HotelDBUtils.DATANAME_UMLIMITSTR.equals(name);
	}

	protected void retriveEnterState() {
		mDirty = false;
		mOldFilterSimpleDataModels.clear();
		if (mFilterModel.selectBrandList != null
				&& !mFilterModel.selectBrandList.isEmpty()) {
			for (FilterSimpleDataModel filter : mFilterModel.selectBrandList) {
				if (isValidateDataName(filter.dataName)) {
					mOldFilterSimpleDataModels.add(filter);
				}
			}
		} else {
			mFilterModel.brandTypeList.clear();
		}
		if (mFilterModel.selectFeatureList != null) {
			for (FilterSimpleDataModel filter : mFilterModel.selectFeatureList) {
				if (isValidateDataName(filter.dataName)) {
					filter.dataType = HotelDBUtils.DATATYPE_FEATURE;
					mOldFilterSimpleDataModels.add(filter);
				}
			}
		}
		if (mFilterModel.selectSelectedAct != null) {
			if (isValidateDataName(mFilterModel.selectSelectedAct.dataName)) {
				mFilterModel.selectSelectedAct.dataType = HotelDBUtils.DATATYPE_SELECTEDACT;
				mOldFilterSimpleDataModels.add(mFilterModel.selectSelectedAct);
			}
		}
		if (mFilterModel.selectAdminReqList != null) {
			for (FilterSimpleDataModel filter : mFilterModel.selectAdminReqList) {
				if (isValidateDataName(filter.dataName)) {
					filter.dataType = HotelDBUtils.DATATYPE_ADMINREQ;
					mOldFilterSimpleDataModels.add(filter);
				}
			}
		}
		if (mFilterModel.selectCommentList != null) {
			for (FilterSimpleDataModel filter : mFilterModel.selectCommentList) {
				if (isValidateDataName(filter.dataName)) {
					mOldFilterSimpleDataModels.add(filter);
				}
			}
		}
		if (mFilterModel.selectDistance != null) {
			if (isValidateDataName(mFilterModel.selectDistance.dataName)) {
				mFilterModel.selectDistance.dataType = HotelDBUtils.DATATYPE_DISTANCE;
				mFilterModel.selectDistance
						.setCompareToSelect(mFilterModel.selectDistance.dataName);
				mOldFilterSimpleDataModels.add(mFilterModel.selectDistance);
			}
		}
		if (mFilterModel.selectPayType != null) {
			if (isValidateDataName(mFilterModel.selectPayType.dataName)) {
				mFilterModel.selectPayType.dataType = HotelDBUtils.DATATYPE_PAYTYPE;
				mOldFilterSimpleDataModels.add(mFilterModel.selectPayType);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mItemHeightBig = activity.getResources().getDimensionPixelSize(
				R.dimen.hotel_filter_item_height_big);
		mItemHeightSmall = activity.getResources().getDimensionPixelSize(
				R.dimen.hotel_filter_item_height_small);
		readDataFromSession();
		retriveEnterState();
		this.initActionCodeData();
		this.initChoiceCount();
		this.initAllowNoSelection();
		mFilterModifier = FilterModifier.newInstance();
		mFilterModifier.inState(mFilterModel);

	}

	@SuppressWarnings("unused")
	private void readDataFromIntent() {
		Activity activity = this.getActivity();
		mFilterModel = (HotelSeniorFilterModel) activity
				.getIntent()
				.getSerializableExtra(HotelConstant.FilterKeys.KEY_Filter_Model);
		mCityModel = (CityModel) activity.getIntent().getSerializableExtra(
				HotelConstant.FilterKeys.KEY_City_Model);
		mBusinessType = activity.getIntent().getIntExtra(
				HotelConstant.FilterKeys.Key_Business_Type,
				HotelConstant.FilterKeys.Business_Inland);
		mCheckInDate = activity.getIntent().getStringExtra(
				HotelConstant.FilterKeys.Key_CheckIn_Day);
		mCheckOutDate = activity.getIntent().getStringExtra(
				HotelConstant.FilterKeys.Key_CheckOut_Day);
		if (mBusinessType == Business_Inland) {
			mHotelDataTypeEnum = HotelDataTypeEnum.INLAND_HOTEL;
		} else if (mBusinessType == Business_Overseas) {
			mHotelDataTypeEnum = HotelDataTypeEnum.OVERSEAS_HOTEL;
		} else if (mBusinessType == Business_WiseHotel) {
			mHotelDataTypeEnum = HotelDataTypeEnum.WISE_HOTEL;
		}
	}

	protected void logPage() {
		if (mBusinessType == Business_Inland) {
			CtripActionLogUtil.logPage("hotel_inland_filter");
		} else if (mBusinessType == Business_Overseas) {
			CtripActionLogUtil.logPage("hotel_oversea_filter");
		}
	}

	private void readDataFromSession() {

		if (Session.getSession().hasAttribute(
				HotelConstant.FilterKeys.KEY_Filter_Model)) {
			mFilterModel = (HotelSeniorFilterModel) Session.getSession()
					.getAttribute(HotelConstant.FilterKeys.KEY_Filter_Model);
		}
		if (Session.getSession().hasAttribute(
				HotelConstant.FilterKeys.KEY_City_Model)) {
			mCityModel = (CityModel) Session.getSession().getAttribute(
					HotelConstant.FilterKeys.KEY_City_Model);
		}
		if (Session.getSession().hasAttribute(
				HotelConstant.FilterKeys.Key_Business_Type)) {
			mBusinessType = (Integer) Session.getSession().getAttribute(
					HotelConstant.FilterKeys.Key_Business_Type);
		}
		if (Session.getSession().hasAttribute(
				HotelConstant.FilterKeys.Key_CheckOut_Day)) {
			mCheckOutDate = (String) Session.getSession().getAttribute(
					HotelConstant.FilterKeys.Key_CheckOut_Day);
		}
		if (Session.getSession().hasAttribute(
				HotelConstant.FilterKeys.Key_CheckIn_Day)) {
			mCheckInDate = (String) Session.getSession().getAttribute(
					HotelConstant.FilterKeys.Key_CheckIn_Day);
		}
		if (mBusinessType == Business_Inland) {
			mHotelDataTypeEnum = HotelDataTypeEnum.INLAND_HOTEL;
		} else if (mBusinessType == Business_Overseas) {
			mHotelDataTypeEnum = HotelDataTypeEnum.OVERSEAS_HOTEL;
		} else if (mBusinessType == Business_WiseHotel) {
			mHotelDataTypeEnum = HotelDataTypeEnum.WISE_HOTEL;
		} else if (mBusinessType == Business_GroupHotel) {
			// mHotelDataTypeEnum = HotelDataTypeEnum.GROUPON_HOTEL;
		}
		logPage();
	}

	protected boolean isSelected(FilterSimpleDataModel keyWord) {
		return false;
	}

	protected void ensureNotLimitedNode(
			ArrayList<FilterSimpleDataModel> keywords) {
		boolean has = false;
		String type = "";
		for (FilterSimpleDataModel model : keywords) {
			type = model.dataType;
			if (HotelDBUtils.DATANAME_UMLIMITSTR.equals(model.dataName)) {
				has = true;
				break;
			}
		}
		if (has) {
			return;
		}
		FilterSimpleDataModel model = new FilterSimpleDataModel();
		model.dataID = "-1";
		model.dataName = HotelDBUtils.DATANAME_UMLIMITSTR;
		model.dataType = type;
		model.dataValue = "-1";
		keywords.add(0, model);
	}

	protected boolean isTabViewModelSelected(KeywordTabViewModel keyword) {
		return false;
	}

	protected int getDefaultMaxSelectCount() {
		return -1;// multi-choice
	}

	protected void initRoot(TreeNode root,
			ArrayList<KeywordTabViewModel> keywords) {
		if (root == null) {
			return;
		}
		if (keywords == null) {
			if (root.getCallback() != null) {
				root.getCallback().onSubTreeLoadFail();
			}
			return;
		}
		KeywordTabViewModel tabModel = (KeywordTabViewModel) root.getValue();
		boolean isBrandTab = tabModel != null
				&& "品牌".equals(tabModel.currentTabName);
		ArrayList<KeywordTabViewModel> keyWords = new ArrayList<KeywordTabViewModel>();
		keyWords.addAll(keywords);
		int i = 0;
		boolean mFirstSelectionFound = false;
		for (i = 0; i < keyWords.size(); i++) {
			KeywordTabViewModel keyword = keyWords.get(i);
			TreeNode node = new TreeNode();
			node.setSelectionShareWithSibling(isBrandTab);
			node.setPadding(DeviceUtil.getPixelFromDip(5));
			node.setValueInflater(new ValueInflater() {
				@Override
				public String inflateLabel(TreeNode node) {
					KeywordTabViewModel key = (KeywordTabViewModel) node
							.getValue();
					return key.currentTabName;
				}

				@Override
				public boolean valueEquals(Object one, Object two) {
					if ((one == null && two != null)
							|| (one != null && two == null)) {
						return false;
					}
					if (one == two) {
						return true;
					}
					if (one instanceof KeywordTabViewModel
							&& two instanceof KeywordTabViewModel) {
						KeywordTabViewModel a = (KeywordTabViewModel) one;
						KeywordTabViewModel b = (KeywordTabViewModel) two;
						return a.currentTabName.equals(b.currentTabName);
					}
					return false;
				}
			});
			node.setValue(keyword);
			node.setLogActionCode(mStaticActionCode.get(keyword.currentTabName));
			node.setMaxSelectCount(mChoiceCount
					.containsKey(keyword.currentTabName) ? mChoiceCount
					.get(keyword.currentTabName) : getDefaultMaxSelectCount());
			node.setAllowNoSelection(mAllowNoSelection
					.containsKey(keyword.currentTabName) ? mAllowNoSelection
					.get(keyword.currentTabName) : true);
			boolean selected = isTabViewModelSelected(keyword);
			if (selected) {
				root.selectChild(node);
			}
			if (keyword.hasNextTab) {
				int pDepth = root.getDepth();
				if (pDepth == 3) {
					node.setLazyDepth(2);
					node.setWidthWeight(0.4f);
					node.setChildHeight(mItemHeightBig);
				} else {
					node.setLazyDepth(pDepth - 1);
					node.setChildHeight(mItemHeightBig);
				}

				node.setSubTreeOpener(new TreeNode.SubTreeOpener() {
					@Override
					public int getSubTreeIndex(TreeNode node) {
						List<TreeNode> children = node.getSubTrees();
						for (int i = 0; i < children.size(); i++) {
							TreeNode child = children.get(i);
							Object value = child.getValue();
							if (!(value instanceof KeywordTabViewModel)) {
								return 0;
							}
							KeywordTabViewModel keyword = (KeywordTabViewModel) value;
							if (isTabViewModelSelected(keyword)) {
								return i;
							}
						}
						return 0;
					}
				});
			} else {
				int pDepth = root.getDepth();
				if (pDepth == 3) {
					node.setChildHeight(mItemHeightSmall);
					node.setLazyDepth(1);
				} else {
					if (!isBrandTab) {
						node.setLazyDepth(pDepth - 1);
					}
					node.setChildHeight(mItemHeightBig);
				}
				node.setDividerColor(Color.parseColor("#dddddd"));
			}
			node.setLazyLoader(mLazyLoader);
			if (root.isRoot()) {
				addSelection(node, keyword);
			}
			root.addSubTree(node);
			if (selected && !mFirstSelectionFound) {
				node.markActive();
				mFirstSelectionFound = true;
			}
		}
		if (root.getCallback() != null) {
			if (root.getSubTrees().size() > 0) {
				root.getCallback().onSubTreesLoaded(root.getSubTrees());
			} else {
				root.getCallback().onSubTreeLoadFail();
			}
		}
	}

	protected void addSelection(TreeNode node, KeywordTabViewModel tab) {
		List<FilterSimpleDataModel> selected = new ArrayList<FilterSimpleDataModel>();
		if (HotelDBUtils.DATATYPE_BRAND.equals(tab.currentTabType)) {
			if (mFilterModel.selectBrandList != null
					&& !mFilterModel.selectBrandList.isEmpty()) {
				selected.addAll(mFilterModel.selectBrandList);
			}
		} else if (HotelDBUtils.DATATYPE_FEATURE.equals(tab.currentTabType)) {
			if (mFilterModel.selectFeatureList != null
					&& !mFilterModel.selectFeatureList.isEmpty()) {
				selected.addAll(mFilterModel.selectFeatureList);
			}
		} else if (HotelDBUtils.DATATYPE_DISTANCE.equals(tab.currentTabType)) {
			if (mFilterModel.selectDistance != null
					&& isValidateDataName(mFilterModel.selectDistance.dataName)) {
				selected.add(mFilterModel.selectDistance);
			}
		} else if (HotelDBUtils.DATATYPE_ADMINREQ.equals(tab.currentTabType)) {
			if (mFilterModel.selectAdminReqList != null
					&& !mFilterModel.selectAdminReqList.isEmpty()) {
				selected.addAll(mFilterModel.selectAdminReqList);
			}
		} else if (HotelDBUtils.DATATYPE_COMMENT.equals(tab.currentTabType)) {
			if (mFilterModel.selectCommentList != null
					&& !mFilterModel.selectCommentList.isEmpty()) {
				selected.addAll(mFilterModel.selectCommentList);
			}
		} else if (HotelDBUtils.DATATYPE_PAYTYPE.equals(tab.currentTabType)) {
			if (mFilterModel.selectPayType != null
					&& isValidateDataName(mFilterModel.selectPayType.dataName)) {
				selected.add(mFilterModel.selectPayType);
			}
		}
		for (FilterSimpleDataModel filter : selected) {
			TreeNode one = new TreeNode();
			one.setValue(filter);
			one.setValueInflater(mValueInflater);
			if (filter.flag == HotelDBUtils.FLAG_FILTERMODEL_FROM_INPUT) {
				one.setIsUnKnownNode(true);
			} else {
				one.setIsPreloadNode(true);
			}
			one.setSelectedByInit();
			node.selectChild(one);
		}
	}

	protected ClickHandler provideClickHandler(TreeNode node,
			FilterSimpleDataModel filter) {
		return null;
	}

	protected ValueInflater mValueInflater = new ValueInflater() {
		@Override
		public String inflateLabel(TreeNode node) {
			FilterSimpleDataModel key = (FilterSimpleDataModel) node.getValue();
			return key.dataName;
		}

		@Override
		public boolean valueEquals(Object one, Object two) {
			if (one == null && two != null || one != null && two == null) {
				return false;
			}
			if (!(one instanceof FilterSimpleDataModel)
					|| !(two instanceof FilterSimpleDataModel)) {
				return false;
			}
			FilterSimpleDataModel a = (FilterSimpleDataModel) one;
			FilterSimpleDataModel b = (FilterSimpleDataModel) two;
			return contentEquals(a, b);
		}
	};

	protected void initLeaf(TreeNode root,
			ArrayList<FilterSimpleDataModel> keywords) {
		if (root == null) {
			return;
		}
		if (keywords == null) {
			if (root.getCallback() != null) {
				root.getCallback().onSubTreeLoadFail();
			}
			return;
		}
		ArrayList<FilterSimpleDataModel> temp = new ArrayList<FilterSimpleDataModel>();
		temp.addAll(keywords);
		keywords = temp;
		ensureNotLimitedNode(keywords);
		ArrayList<FilterSimpleDataModel> keyWords = keywords;
		int i = 0;
		root.removeAllSubTree();
		boolean mFirstSelectionFound = false;
		TreeNode noLimit = null;
		for (i = 0; i < keyWords.size(); i++) {
			FilterSimpleDataModel keyword = keyWords.get(i);
			TreeNode node = new TreeNode();
			if (HotelDBUtils.DATANAME_UMLIMITSTR.equals(keyword.dataName)) {
				node.isNoLimitNode = true;
				noLimit = node;
			} else if (StringUtil.toInt(keyword.dataValue) < -1) {
				node.isAllNode = true;
			}
			node.setPadding(DeviceUtil.getPixelFromDip(9));
			int pDepth = root.getDepth();
			if (pDepth == 2) {
				node.setChildHeight(mItemHeightBig);
				node.setLazyDepth(1);
			} else {
				node.setLazyDepth(pDepth - 1);
				node.setChildHeight(mItemHeightBig);
			}

			node.setValueInflater(mValueInflater);

			node.setValue(keyword);
			node.setClickHandler(provideClickHandler(node, keyword));
			root.addSubTree(node);
			boolean isSelected = isSelected(keyword);
			if (isSelected) {
				root.selectChild(node);
				node.setSelectedByInit();
				if (root.getParent() != null) {
					root.getParent().removePreloadNode(node);
				}
			} else if (root.isSelectionShareWithSibling()) {
				List<TreeNode> selection = root.getSiblingSelection();
				if (selection.contains(node)) {
					isSelected = true;
					root.selectChild(node);
				}
			}

			node.setMaxSelectCount(1);
			node.setDividerColor(Color.parseColor("#dddddd"));
			// root.addSubTree(node);
			if (isSelected && !mFirstSelectionFound) {
				node.markActive();
				mFirstSelectionFound = true;
			}
		}
		if (!root.hasSelectedChild()
				&& noLimit != null
				&& !(root.hasUnknownSelection() || (root.getParent() != null && root
						.getParent().hasUnknownSelection()))) {
			// KeywordTabViewModel tabModel = (KeywordTabViewModel)
			// root.getValue();
			// if (tabModel != null &&
			// this.getDataModel(mOldFilterSimpleDataModels,
			// tabModel.currentTabType) == null) {
			noLimit.setSelected(true);
			// }
		}
		if (root.isAllowNoSelection()) {
			// addUnknownSelectionIfExist(root,keywords);
		}
		if (root.getCallback() != null) {
			if (root.getSubTrees().size() > 0) {
				root.getCallback().onSubTreesLoaded(root.getSubTrees());
			} else {
				root.getCallback().onSubTreeLoadFail();
			}
		}
	}

	private void addUnknownSelectionIfExist(TreeNode root,
			List<FilterSimpleDataModel> known) {
		List<FilterSimpleDataModel> selected = new ArrayList<FilterSimpleDataModel>();
		KeywordTabViewModel tabModel = (KeywordTabViewModel) root.getValue();
		if (tabModel == null) {
			return;
		}
		for (FilterSimpleDataModel filter : mOldFilterSimpleDataModels) {
			if (filter.dataType.equals(tabModel.currentTabType)) {
				selected.add(filter);
			}
		}
		List<FilterSimpleDataModel> unkownSelections = new ArrayList<FilterSimpleDataModel>();
		for (FilterSimpleDataModel selection : selected) {
			boolean found = false;
			for (FilterSimpleDataModel kn : known) {
				if (kn.getCompareToSelect().equals(
						selection.getCompareToSelect())) {
					found = true;
					break;
				}
			}
			if (!found) {
				unkownSelections.add(selection);
			}
		}
		for (FilterSimpleDataModel filter : unkownSelections) {
			TreeNode node = new TreeNode();
			node.setValue(filter);
			node.setSelectedByInit();
			node.setIsUnKnownNode(true);
			node.setValueInflater(mValueInflater);
			root.selectChild(node);
		}
	}

	private LazyLoader mLazyLoader = new LazyLoader() {
		@Override
		public void lazyLoad(TreeNode node) {
			mActiveNode = node;
			requestService();
		}

		@Override
		public void justCancelLastRequest() {
			if (mLastSendResultModel != null) {
				cancelOtherSession(sMainUnit, mLastSendResultModel.getToken());
				mLastSendResultModel = null;
			}
		}
	};

	@Override
	protected void onRefresh() {
		HashMap<HotelDataTypeEnum, HotelSeniorFilterDataModel> modelMap = mScreeningCacheBean.standardFilterList;
		ArrayList<KeywordTabViewModel> keyWords = null;
		ArrayList<FilterSimpleDataModel> simpleWords = null;
		HotelSeniorFilterDataModel listModel = null;
		if (modelMap != null) {
			if (mHotelDataTypeEnum == HotelDataTypeEnum.INLAND_HOTEL) {
				listModel = modelMap.get(HotelDataTypeEnum.INLAND_HOTEL);
			} else if (mHotelDataTypeEnum == HotelDataTypeEnum.OVERSEAS_HOTEL) {
				listModel = modelMap.get(HotelDataTypeEnum.OVERSEAS_HOTEL);
			} else if (mHotelDataTypeEnum == HotelDataTypeEnum.WISE_HOTEL) {
				listModel = modelMap.get(HotelDataTypeEnum.WISE_HOTEL);
			}
			if (mActiveNode != null) {
				KeywordTabViewModel k = (KeywordTabViewModel) mActiveNode
						.getValue();
				if (k.hasNextTab) {
					keyWords = listModel.getNextTabList(k);
				} else {
					simpleWords = listModel.getKeywordDataList(k);
				}
			}
		}

		if (mActiveNode == null) {
			TreeNode temp = mActiveNode;
			mActiveNode = null;
			initRoot(temp, keyWords);
		} else if (mActiveNode.getDepth() > 1) {
			if (keyWords == null || keyWords.isEmpty()) {
				simpleWords = new ArrayList<FilterSimpleDataModel>();
				this.ensureNotLimitedNode(simpleWords);
				mActiveNode.setLazyDepth(1);
				mActiveNode.setWidthWeight(0);
				mActiveNode.setChildHeight(mItemHeightSmall);
				mActiveNode.setDividerColor(Color.parseColor("#dddddd"));
				initLeaf(mActiveNode, simpleWords);
			} else {
				TreeNode temp = mActiveNode;
				mActiveNode = null;
				initRoot(temp, keyWords);
				if (temp != null) {
					KeywordTabViewModel k = (KeywordTabViewModel) temp
							.getValue();
					if (k != null && "品牌".equals(k.currentTabName)) {
						List<TreeNode> nodes = temp.getSubTrees();
						for (TreeNode node : nodes) {
							KeywordTabViewModel sub = (KeywordTabViewModel) node
									.getValue();
							ArrayList<FilterSimpleDataModel> leafSimpleWords = listModel
									.getKeywordDataList(sub);
							initLeaf(node, leafSimpleWords);
						}
						for (TreeNode node : nodes) {
							if (node.hasUnknownSelection()
									|| temp.hasUnknownSelection()) {
								// node.setCheckNolimitWhenEmpty(false);
							}
						}
					}
				}
			}
		} else {
			if (simpleWords == null) {
				simpleWords = new ArrayList<FilterSimpleDataModel>();
			}
			TreeNode temp = mActiveNode;
			mActiveNode = null;
			initLeaf(temp, simpleWords);
		}
	}

	@Override
	protected SenderResultModel onRequetService() {
		SenderResultModel model = send();
		mLastSendResultModel = model;
		super.cancelOtherSession(sMainUnit, mLastSendResultModel.getToken());
		return model;
	}

	protected String sMainUnit = "FilterFragment";
	private SenderResultModel mLastSendResultModel;

	protected abstract SenderResultModel send();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.hotel_fragment_tree_layout,
				null);
	}

	protected boolean mDirty = false;

	protected boolean contains(List<FilterSimpleDataModel> filters,
			FilterSimpleDataModel model) {
		for (FilterSimpleDataModel filter : filters) {
			if (contentEquals(filter, model)) {
				return true;
			}
		}
		return false;
	}

	protected void addTo(List<FilterSimpleDataModel> filters,
			FilterSimpleDataModel model) {
		if (!contains(filters, model)) {
			filters.add(model);
		}
	}

	protected void removeFrom(List<FilterSimpleDataModel> filters,
			FilterSimpleDataModel model) {
		ArrayList<FilterSimpleDataModel> all = new ArrayList<FilterSimpleDataModel>();
		all.addAll(filters);
		for (FilterSimpleDataModel filterModel : all) {
			if (contentEquals(filterModel, model)) {
				filters.remove(filterModel);
			}
		}
	}

	protected boolean contentEquals(FilterSimpleDataModel a,
			FilterSimpleDataModel b) {
		if (a == null && b != null || a != null && b == null) {
			return false;
		}
		return a.getCompareToSelect().equals(b.getCompareToSelect());
	}

	public FilterSimpleDataModel getDataModel(
			List<FilterSimpleDataModel> filters, String type) {
		for (FilterSimpleDataModel model : filters) {
			String dataType = model.dataType;
			if (dataType.equals(type)) {
				return model;
			}
			if (HotelDBUtils.DATATYPE_COMMENT.equals(type)) {
				if (HotelDBUtils.DATATYPE_COMMENT_COUNT.equals(dataType)
						|| HotelDBUtils.DATATYPE_SCORE.equals(dataType)) {
					return model;
				}
			}
		}
		return null;
	}

	public FilterSimpleDataModel getDataModel(String type) {
		return getDataModel(mOldFilterSimpleDataModels, type);
	}

	private OnTitleClickListener mOnTitleClickListener = new CtripTitleView.OnTitleClickListener() {
		@Override
		public void onTitleClick(View v) {
		}

		@Override
		public void onLogoClick(View v) {
		}

		@Override
		public void onButtonClick(View v) {
			CtripActionLogUtil.logCode("c_restore");
			reset();
		}
	};

	protected void reset() {
		// mFilterModel.reset(-1);
		mOldFilterSimpleDataModels.clear();
		mDirty = true;
		mRootNode.reset();
		List<TreeNode> allLeaves = mRootNode.getAllLeavies();
		for (TreeNode node : allLeaves) {
			FilterSimpleDataModel model = (FilterSimpleDataModel) node
					.getValue();
			if (HotelDBUtils.DATANAME_UMLIMITSTR.equals(model.dataName)) {
				node.setSelected(true);
			}
		}
		mTreeView.refresh();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// eat the ontouchevent
			}
		});
		mTreeView = (TreeView) view.findViewById(R.id.mTreeView);
		mSubmit = (Button) view.findViewById(R.id.mSubmit);
		mTitleView = (CtripTitleView) view.findViewById(R.id.mTitleView);
		mTitleView.setOnTitleClickListener(mOnTitleClickListener);
		mSubmit = (Button) view.findViewById(R.id.mSubmit);
		mSubmit.setVisibility(View.VISIBLE);
		mSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CtripActionLogUtil.logCode("c_confirm");
				onSubmit();
			}
		});
		mRootNode = new TreeNode();
		initTopRoot();
	}

	protected void onSubmit() {
	}

	protected int getDepth() {
		return 3;
	}

	protected void initTopRoot() {
		HashMap<HotelDataTypeEnum, HotelSeniorFilterDataModel> modelMap = mScreeningCacheBean.standardFilterList;
		mRootNode.setLazyDepth(getDepth());
		if (modelMap != null) {
			HotelSeniorFilterDataModel listModel = null;
			if (mHotelDataTypeEnum == HotelDataTypeEnum.INLAND_HOTEL) {
				listModel = modelMap.get(HotelDataTypeEnum.INLAND_HOTEL);
			} else if (mHotelDataTypeEnum == HotelDataTypeEnum.OVERSEAS_HOTEL) {
				listModel = modelMap.get(HotelDataTypeEnum.OVERSEAS_HOTEL);
			} else if (mHotelDataTypeEnum == HotelDataTypeEnum.WISE_HOTEL) {
				listModel = modelMap.get(HotelDataTypeEnum.WISE_HOTEL);
			}
			ArrayList<KeywordTabViewModel> keyWords = listModel.filterTabList;
			initRoot(mRootNode, keyWords);
		}
		mRootNode.setDividerColor(Color.parseColor("#dddddd"));
		mTreeView.setData(mRootNode);
		mRootNode.setChildHeight(mItemHeightBig);
		mRootNode.setWidthWeight(0.28f);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mTreeView.openSubTree(mRootNode.getActivieIndex());
			}
		}, 100);
	}
}
*/