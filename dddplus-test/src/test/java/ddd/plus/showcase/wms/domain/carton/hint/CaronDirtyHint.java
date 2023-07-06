package ddd.plus.showcase.wms.domain.carton.hint;

import ddd.plus.showcase.wms.domain.carton.Carton;
import io.github.dddplus.model.IDirtyHint;
import io.github.dddplus.model.IMergeAwareDirtyHint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.BitSet;

@Getter
public class CaronDirtyHint implements IMergeAwareDirtyHint<Long> {
    private static int BITS = 8;

    @Getter(AccessLevel.PRIVATE)
    private BitSet dirtyMap = new BitSet(BITS);

    @AllArgsConstructor
    public enum Type {
        BindOrder(1),
        FromContainer(2),
        Fulfill(3),
        InstallConsumables(4);
        int bit;

        BitSet dirtyMap() {
            BitSet s = new BitSet(BITS);
            s.set(bit);
            return s;
        }
    }

    private final Carton carton;

    @Setter
    private BigDecimal checkedQty; // 冗余字段，该箱总计货品数量：为了便于数据库查询、排序

    public CaronDirtyHint(Carton carton, Type type) {
        this.carton = carton;
        this.dirtyMap.set(type.bit);
    }

    /**
     * 是否包括了指定类型.
     */
    public boolean has(Type type) {
        return dirtyMap.intersects(type.dirtyMap());
    }

    @Override
    public void onMerge(IDirtyHint thatHint) {
        CaronDirtyHint that = (CaronDirtyHint) thatHint;
        that.dirtyMap.or(this.dirtyMap);
        if (this.checkedQty != null) {
            that.checkedQty = this.checkedQty;
        }
    }

    @Override
    public Long getId() {
        return carton.getId();
    }
}
