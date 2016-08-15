/**
 * Created by ElisaPaz on 11-08-2016.
 */

var app = angular.module('KioskitoCaDCCApp', ['ui.bootstrap', 'nvd3']);

app.controller('newPurchaseCtrl', function($scope, $http){
    $http.get('/getProductsNames').success(function(data){
        $scope.names = data;
    });

    $scope.bag = [
        {
            name: "",
            packages: 0,
            quantityPerPackage: 0,
            purchasePrice: 0,
            salePrice: 0
        },
    ];


    $scope.newProduct = function(){
        $scope.bag.push(
            {
                name: "",
                packages: 0,
                quantityPerPackage: 0,
                purchasePrice: 0,
                salePrice: 0
            }
        )
    }

    $scope.removeProduct = function (index) {
        $scope.bag.splice(index, 1);
    };
});

app.controller('financesChartCtrl', function($scope){

});

app.controller('soldProductsChartCtrl', function($scope) {

});

app.controller('totalConsCaloriesChartCtrl', function($scope){
    $scope.options = {
        chart: {
            type: 'lineChart',
            height: 450,
            margin : {
                top: 20,
                right: 20,
                bottom: 50,
                left: 60
            },
            x: function(d){return d[0];},
            y: function(d){return d[1];},
            useInteractiveGuideline: true,
            showLegend: false,
            dispatch: {
                stateChange: function(e){ console.log("stateChange"); },
                changeState: function(e){ console.log("changeState"); },
                tooltipShow: function(e){ console.log("tooltipShow"); },
                tooltipHide: function(e){ console.log("tooltipHide"); }
            },
            xAxis: {
                axisLabel: 'Fecha',
                tickFormat: function(d) {
                    return d3.time.format('%x')(new Date(d))
                },
                showMaxMin: false
            },
            yAxis: {
                axisLabel: 'Calorías',
                tickFormat: function(d){
                    return d3.format('.02f')(d);
                },
                showMaxMin: false
            }
        },
        title: {
            enable: true,
            text: 'Total de calorías consumidas entre fechas'
        }
    }

    $scope.data = [
        {
            "key" : "Calorías" ,
            "values" : [ [ 1025409600000 , 23.041422681023] , [ 1028088000000 , 19.854291255832] , [ 1030766400000 , 21.02286281168] , [ 1033358400000 , 22.093608385173] , [ 1036040400000 , 25.108079299458] , [ 1038632400000 , 26.982389242348] , [ 1041310800000 , 19.828984957662]]
        }
    ]
});

app.controller('detailedConsCaloriesChartCtrl', function($scope) {
    $scope.options = {
        chart: {
            type: 'stackedAreaChart',
            height: 450,
            margin : {
                top: 20,
                right: 20,
                bottom: 50,
                left: 60
            },
            x: function(d){return d[0];},
            y: function(d){return d[1];},
            useVoronoi: false,
            clipEdge: true,
            duration: 100,
            useInteractiveGuideline: true,
            showControls: false,
            xAxis: {
                axisLabel: 'Fecha',
                showMaxMin: false,
                tickFormat: function(d) {
                    return d3.time.format('%x')(new Date(d))
                }
            },
            yAxis: {
                axisLabel: 'Calorías',
                showMaxMin: false,
                tickFormat: function(d){
                    return d3.format(',.2f')(d);
                }
            },
            zoom: {
                enabled: false,
                scaleExtent: [1, 10],
                useFixedDomain: false,
                useNiceScale: false,
                horizontalOff: false,
                verticalOff: true,
                unzoomEventType: 'dblclick.zoom'
            }
        },
        title: {
            enable: true,
            text: 'Productos que más calorías aportan al consumo'
        }
    };

    $scope.data = [
        {
            "key" : "Super 8" ,
            "values" : [ [ 1025409600000 , 23.041422681023] , [ 1028088000000 , 19.854291255832] , [ 1030766400000 , 21.02286281168] , [ 1033358400000 , 22.093608385173] , [ 1036040400000 , 25.108079299458] , [ 1038632400000 , 26.982389242348] , [ 1041310800000 , 19.828984957662] , [ 1043989200000 , 19.914055036294]]
        },

        {
            "key" : "Morocha" ,
            "values" : [ [ 1025409600000 , 7.9356392949025] , [ 1028088000000 , 7.4514668527298] , [ 1030766400000 , 7.9085410566608] , [ 1033358400000 , 5.8996782364764] , [ 1036040400000 , 6.0591869346923] , [ 1038632400000 , 5.9667815800451] , [ 1041310800000 , 8.65528925664] , [ 1043989200000 , 8.7690763386254]]
        },

        {
            "key" : "Golazo" ,
            "values" : [ [ 1025409600000 , 7.9149900245423] , [ 1028088000000 , 7.0899888751059] , [ 1030766400000 , 7.5996132380614] , [ 1033358400000 , 8.2741174301034] , [ 1036040400000 , 9.3564460833513] , [ 1038632400000 , 9.7066786059904] , [ 1041310800000 , 10.213363052343] , [ 1043989200000 , 10.285809585273]]
        },

        {
            "key" : "Jugo Andina" ,
            "values" : [ [ 1025409600000 , 13.153938631352] , [ 1028088000000 , 12.456410521864] , [ 1030766400000 , 12.537048663919] , [ 1033358400000 , 13.947386398309] , [ 1036040400000 , 14.421680682568] , [ 1038632400000 , 14.143238262286] , [ 1041310800000 , 12.229635347478] , [ 1043989200000 , 12.508479916948]]
        } ,

        {
            "key" : "Lay's" ,
            "values" : [ [ 1025409600000 , 9.3433263069351] , [ 1028088000000 , 8.4583069475546] , [ 1030766400000 , 8.0342398154196] , [ 1033358400000 , 8.1538966876572] , [ 1036040400000 , 10.743604786849] , [ 1038632400000 , 12.349366155851] , [ 1041310800000 , 10.742682503899] , [ 1043989200000 , 11.360983869935]]
        } ,

        {
            "key" : "NIK" ,
            "values" : [ [ 1025409600000 , 5.1162447683392] , [ 1028088000000 , 4.2022848306513] , [ 1030766400000 , 4.3543715758736] , [ 1033358400000 , 5.4641223667245] , [ 1036040400000 , 6.0041275884577] , [ 1038632400000 , 6.6050520064486] , [ 1041310800000 , 5.0154059912793] , [ 1043989200000 , 5.1835708554647]]
        } ,

        {
            "key" : "Gansito" ,
            "values" : [ [ 1025409600000 , 1.3503144674343] , [ 1028088000000 , 1.2232741112434] , [ 1030766400000 , 1.3930470790784] , [ 1033358400000 , 1.2631275030593] , [ 1036040400000 , 1.5842699103708] , [ 1038632400000 , 1.9546996043116] , [ 1041310800000 , 0.8504048300986] , [ 1043989200000 , 0.85340686311353]]
        }

    ]
});


