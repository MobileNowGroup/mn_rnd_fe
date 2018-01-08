import React from "react";

const ItemCategory = props => {
  const { categoryIcons, categoryName, bg } = props;
  return (
    <div
      className="item-category"
      style={{
        backgroundImage: `url(${bg})`
      }}
    >
      <img
        className="item-category__icon scaled-img"
        src={categoryIcons[0]}
        srcset={`${categoryIcons[1]} 2x, ${categoryIcons[2]} 3x`}
        alt=""
      />
      <h2 className="item-category__name">{categoryName}</h2>
      <h3 className="item-category__collection">Collection</h3>
    </div>
  );
};

export default ItemCategory;
