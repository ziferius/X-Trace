function XTraceCompareViz(attach, data, params) {
    // Add the data necessary to colour the compare graphs
    var data_a = data[0];
    var data_b = data[1];
    
    var kernel = new WeisfeilerLehmanKernel();
    var kga = yarnchild_kernelgraph_for_trace(data_a);
    var kgb = yarnchild_kernelgraph_for_trace(data_b);
    this.scores = kernel.calculate_node_stability(kga, kgb, "both");
    
    var labels_a = this.scores[0].labels, labels_b = this.scores[1].labels;
    var scores_a = this.scores[0].scores, scores_b = this.scores[1].scores;
    
    data_a.reports.forEach(function(report) {
        if (!report.hasOwnProperty("X-Trace")) return;
        var id = report["X-Trace"][0].substr(18);
        if (labels_a.hasOwnProperty(id)) {
            report["KernelLabels"] = labels_a[id];
        }
        if (scores_a.hasOwnProperty(id)) {
            report["KernelScore"] = [scores_a[id] / labels_a[id].length];
        }
    })
    
    data_b.reports.forEach(function(report) {
        if (!report.hasOwnProperty("X-Trace")) return;
        var id = report["X-Trace"][0].substr(18);
        if (labels_b.hasOwnProperty(id)) {
            report["KernelLabels"] = labels_b[id];
            if (scores_b.hasOwnProperty(id)) {
                report["KernelScore"] = [scores_b[id] / labels_b[id].length];
            }
        }
    })

    // Set up the divs to attach the child graphs
    d3.select(attach).classed("compare-visualization", true);
    var left = d3.select(attach).append("span").classed("left", true).node();
    var right = d3.select(attach).append("span").classed("right", true).node();
    
    // Do the child graphs
    this.left_dag = new XTraceDAG(left, data_a.reports, params);
    this.right_dag = new XTraceDAG(right, data_b.reports, params);
    
    var kernelscore_opacity = function(d) {
        if (d.report["KernelScore"]) {
            var score = d.report["KernelScore"][0];
            return 0.2 + 6 * Math.pow(1-score, 4);
        }
        return 1;
    }
    
    var edge_kernelscore_opacity = function(d) {
        return kernelscore_opacity(d.source) + kernelscore_opacity(d.target) / 2;
    }
    
    // Set the opacity of the nodes
    d3.select(left).selectAll(".node").attr("opacity", kernelscore_opacity);
    d3.select(right).selectAll(".node").attr("opacity", kernelscore_opacity);
    d3.select(left).selectAll(".edge").attr("opacity", edge_kernelscore_opacity);
    d3.select(right).selectAll(".edge").attr("opacity", edge_kernelscore_opacity);
    
    
}