 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService ,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//录入商品
	$scope.add=function(){
		goodsService.add().success(
			function(response){
				if(response.success){
					//重新查询 
		        	alert("新增商品成功");
		        	$scope.entity={}; //清空
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.selectIds=[];
                    $scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={auditStatus:'0'}//定义搜索对象
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
    $scope.itemCatList=[];//商品分类列表
    //查询商品分类
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id ]=response[i].name;
                }
            }
        );
    }

    // 审核的方法:
    $scope.updateStatus = function(status){
        goodsService.updateStatus($scope.selectIds,status).success(
        	function(response) {
        		//成功
        		if(response.success){
        			//提示成功
                    alert(response.message);
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
				}else{
                    alert(response.message);
				}
            }
		);
	}
});
