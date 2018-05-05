com.doubeye.constant = {
	POSITION : {
		LEFT : 'left',
		RIGHT : 'right',
		BOTH_HORIZONTAL : 'both_horizontal',
		TOP : 'top',
		BOTTOM : 'bottom',
		BOTH_VERTICAL : 'both_vertical'
	},
	CSS_ALIGN : {
		HORIZONTAL_ALIGN : {
			LEFT : 'left',
			RIGHT : 'right',
			CENTER : 'center'
		},
		VERTICAL_ALIGN : {
			TOP : 'top',
			BOTTOM : 'bottom',
			MIDDLE : 'middle'
		}
	},
	DB : {
		ORDER_BY : {
			NONE : '',
			ASC : 'ASC',
			DESC : 'DESC'
		},
		/**
		 * 元数据管理下的表状态 
		 */
		TABLE_STATUS : {
			UNCREATED : '未创建',
			CREATED : '已创建',
			META_MANAGED : '已加入元数据管理',
			UNKNOWN : '未知',
			OUT_OF_DATE : '失去同步',
			INVALID_STATUS : '无效的状态',
			META_MANAGED_BUT_DROPPED : '物理表被删除'
		}
	},
	DATA : {
		/**
		 * com.doubeye.Record对象的状态的枚举
		 */
		RECORDSTATUS : {
			NORMAL : 0, //正常状态，与数据库同步，当数据删除时转到MARKDELETED状态，当数据修改时转到MODIFIED状态
			ADDED : 1, //新增数据，数据仅保存在客户端，当数据在客户端新建时为该状态，当数据保存后状态转为NORMAL
			MARKDELETED : 2, //标记为删除的数据，尚未删除，当数据同步到数据库时，数据被删除
			MODIFIED : 3, //修改过的数据，尚未同步到数据库中，当数据同步到数据库中，则状态转为NORMAL
			NEW_AND_DELETED : 4,//新增的数据被删除后为此状态，在数据集合返回修改变动对象时，此数据不出现，可通过将其restor会ADDED状态
			MODIFIED_AND_DELETE : 5//修改后被删除，在数据集合返回修改变动对象时，数据会标记为DELETE数据，当从DELETE状态改回时，会变成MODIFY状态
		},
		DATATYPE : {
			NUMBER : 0,
			BOOLEAN : 1,
			STRING : 2
		}
	},
	SAVE_ACTION : {
		ADD : 0,
		MODIFY : 1
	}
};
