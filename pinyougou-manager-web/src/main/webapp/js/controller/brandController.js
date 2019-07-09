app.controller('brandController',function($scope,$controller,brandService) {

    <!--继承-->
    $controller('baseController',{$scope:$scope});


    //查询品牌列表
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页查询列表
    $scope.findPage=function (page,size) {
        brandService.findPage(page,size).success(
            function (response) {
                $scope.list=response.rows;  //显示当前页的数据
                $scope.paginationConf.totalItems=response.total; //更新总记录数
            }
        );
    }

    //添加品牌
    $scope.save=function(){

        var serviceObject=null;
        if($scope.entity.id!=null){//如果有ID
            serviceObject=brandService.update($scope.entity);
        }else{
            serviceObject=brandService.add($scope.entity)
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        );

    }

    //查询单个实体
    $scope.findOne=function (id) {

        brandService.findOne(id).success(
            function(response){
                $scope.entity= response;
            }
        );
    }


    //批量删除
    $scope.dele=function() {
        //获取选中的复选框
        if (confirm("确定要删除吗？")) {
            brandService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//重新加载列表
                        $scope.selectIds=[];
                    } else {
                        alert(response.message);
                    }
                }
            );
        }
    }

    //条件查询
    $scope.searchEntity={};
    $scope.search=function (page,size) {

        brandService.search(page,size,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;  //显示当前页的数据
                $scope.paginationConf.totalItems=response.total; //更新总记录数
            }
        );
    }

});