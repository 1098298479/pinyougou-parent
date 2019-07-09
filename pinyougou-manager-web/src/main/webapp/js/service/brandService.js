app.service('brandService',function ($http) {

    this.findAll=function () {
        return  $http.get('../brand/findAll.do');
    }

    this.findPage=function (page,size) {
        return $http.get('../brand/findPage.do?page='+page+'&size='+size);
    }

    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    }

    this.dele=function (ids) {
        return $http.get('../brand/deleteSelected.do?ids=' + ids);
    }

    this.search=function (page,size,searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&size='+size,searchEntity);
    }


    this.add=function (entity) {
        return $http.post('../brand/addBrand.do',entity )
    }

    this.update=function (entity) {
        return $http.post('../brand/updateBrand.do',entity )
    }

    this.selectOptionList=function(){
        return $http.get('../brand/selectOptionList.do');
    }




});