$(function(){

    let timer;

    $("#keyword").on("keyup", function(){

        clearTimeout(timer);

        let keyword = $(this).val();
        let category = $("#category").val();

        if(keyword.trim().length < 1){
            $("#searchResult").hide();
            return;
        }

        timer = setTimeout(function(){

            $.ajax({

                url : "/book/search",

                type : "GET",

                data : {
                    category : category,
                    keyword : keyword
                },

                success : function(data){

                    if(data.length === 0){
                        $("#searchResult").hide();
                        return;
                    }

                    let html = "";

                    $.each(data, function(i, book){

					html += `
					<div class="search-item"
					     data-image="${book.bookimage}"
					     data-title="${book.title}"
					     data-author="${book.author}"
					     data-price="${book.price}">
					
					    <a href="/book/view?id=${book.id}">
					        ${book.title}
					    </a>
					
					</div>
					`;
                    });

                    $("#searchList").html(html);

					$("#searchResult").css("display","flex");
					
					const firstBook = data[0];
					
					$("#previewImage")
					    .attr("src", firstBook.bookimage)
					    .show();
					
					$(".preview-title").text(firstBook.title);
					$(".preview-author").text(firstBook.author);
					
					$(".preview-price").text(
					    Number(firstBook.price).toLocaleString() + "원"
					);
                },

                error : function(){
                    console.log("검색 실패");
                }

            });

        }, 300);

    });

    $(document).click(function(e){

        if(!$(e.target).closest(".search-wrapper").length){
            $("#searchResult").hide();
        }

    });
    
    $(document).on(
    "mouseenter",
    ".search-item",
    function(){

        $("#previewImage")
            .attr("src", $(this).data("image"));

        $(".preview-title")
            .text($(this).data("title"));

        $(".preview-author")
            .text($(this).data("author"));

        $(".preview-price")
            .text(
                $(this).data("price").toLocaleString()
                + "원"
            );

    }
);

});